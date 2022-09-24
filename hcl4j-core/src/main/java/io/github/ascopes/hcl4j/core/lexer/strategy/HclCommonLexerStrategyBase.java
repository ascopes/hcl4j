/*
 * Copyright (C) 2022 - 2022 Ashley Scopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ascopes.hcl4j.core.lexer.strategy;

import io.github.ascopes.hcl4j.core.ex.HclBadTokenException;
import io.github.ascopes.hcl4j.core.ex.HclStreamException;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.intern.RawContentBuffer;
import io.github.ascopes.hcl4j.core.lexer.HclDefaultLexer;
import io.github.ascopes.hcl4j.core.lexer.HclLexerStrategy;
import io.github.ascopes.hcl4j.core.tokens.HclDefaultToken;
import io.github.ascopes.hcl4j.core.tokens.HclEofToken;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.io.IOException;

/**
 * Abstract implementation of a lexer strategy with some common behaviours pre-implemented.
 *
 * <p>Lexer strategies should usually derive from this class for simplicity.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class HclCommonLexerStrategyBase implements HclLexerStrategy {

  protected final HclDefaultLexer context;

  @Nullable
  protected HclEofToken cachedEofToken;

  /**
   * Initialize this strategy.
   *
   * @param context the lexer context to use.
   */
  protected HclCommonLexerStrategyBase(HclDefaultLexer context) {
    this.context = context;

    // We fill this after we hit EOF to prevent allocating lots of duplicate object descriptors
    // for the end of the file. This is just a minor optimisation.
    cachedEofToken = null;
  }

  /**
   * Construct a token from the given number of characters.
   *
   * @param type   the type of token to use.
   * @param length the character count to read.
   * @return the token.
   * @throws HclStreamException if an {@link IOException} occurred internally.
   */
  protected HclToken newToken(HclTokenType type, int length) throws HclStreamException {
    var start = context.charSource().location();
    var raw = context.charSource().readString(length);
    var end = context.charSource().location();

    assert raw.length() == length : "EOF reached prematurely, missing check occurred elsewhere";
    return new HclDefaultToken(type, raw, start, end);
  }

  /**
   * Construct an error from the given number of characters.
   *
   * @param message the error message to use.
   * @param length  the character count to read.
   * @return the error to raise.
   * @throws HclStreamException if an IO error occurs reading the erroneous content.
   */
  protected HclBadTokenException syntaxError(String message, int length)
      throws HclStreamException {
    var start = context.charSource().location();
    var raw = context.charSource().readString(length);
    var end = context.charSource().location();

    assert raw.length() == length : "EOF reached prematurely, missing check occurred elsewhere";
    return new HclBadTokenException(context.charSource().name(), raw, start, end, message);
  }

  /**
   * Consume an end-of-file token.
   *
   * @return the token.
   */
  protected HclToken consumeEndOfFile() {
    return cachedEofToken == null
        ? (cachedEofToken = new HclEofToken(context.charSource().location()))
        : cachedEofToken;
  }

  /**
   * Consume an unknown character and emit it as an error.
   *
   * @return the error to raise.
   * @throws HclStreamException if preparing the exception failed with an {@link IOException}.
   */
  protected HclBadTokenException errorUnrecognisedCharacter() throws HclStreamException {
    throw syntaxError("Unrecognised character in input", 1);
  }

  /**
   * Consume an identifier, given the assumption that the first character is an ID_START.
   *
   * @return the token.
   * @throws HclStreamException if an {@link HclStreamException} occurs during parsing.
   */
  protected HclToken consumeIdentifier() throws HclStreamException {
    var start = context.charSource().location();
    var buff = new RawContentBuffer()
        .append(context.charSource().read());

    while (true) {
      var next = context.charSource().peek(0);
      if (isIdContinue(next)) {
        buff.append(next);
        context.charSource().advance(1);
      } else {
        break;
      }
    }

    var end = context.charSource().location();

    return new HclDefaultToken(HclTokenType.IDENTIFIER, buff.content(), start, end);
  }

  /**
   * Consume some whitespace, given the assumption that the current character is whitespace.
   *
   * @return the token.
   * @throws HclStreamException if an {@link IOException} occurs during parsing.
   */
  protected HclToken consumeWhitespace() throws HclStreamException {
    var start = context.charSource().location();
    var buff = new RawContentBuffer()
        .append(context.charSource().read());

    while (true) {
      var nextChar = context.charSource().peek(0);

      if (nextChar != ' ' && nextChar != '\t') {
        break;
      }

      buff.append(nextChar);
      context.charSource().advance(1);
    }

    var end = context.charSource().location();

    return new HclDefaultToken(HclTokenType.WHITESPACE, buff.content(), start, end);
  }

  /**
   * Consume some whitespace, given the assumption that the current character is a newline start. or
   * a line feed.
   *
   * @return the token.
   * @throws HclStreamException if an {@link IOException} occurs during parsing.
   */
  protected HclToken consumeNewLine() throws HclStreamException {
    return switch (context.charSource().peek(0)) {
      case '\r' -> {
        if (context.charSource().peek(1) == '\n') {
          yield newToken(HclTokenType.NEW_LINE, 2);
        } else {
          throw errorUnrecognisedCharacter();
        }
      }
      case '\n' -> newToken(HclTokenType.NEW_LINE, 1);
      default -> throw errorUnrecognisedCharacter();
    };
  }

  /**
   * Determine if the code point is an HCL ID_START character.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ID_START character, or {@code false} otherwise.
   */
  protected static boolean isIdStart(int codePoint) {
    return Character.isUnicodeIdentifierStart(codePoint);
  }

  /**
   * Determine if the code point is an HCL ID_CONTINUE character.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ID_CONTINUE character, or {@code false} otherwise.
   */
  protected static boolean isIdContinue(int codePoint) {
    return Character.isUnicodeIdentifierPart(codePoint) || codePoint == '-';
  }

  /**
   * Determine if the code point is an ASCII digit.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ASCII digit, or {@code false} otherwise.
   */
  protected static boolean isDigit(int codePoint) {
    return '0' <= codePoint && codePoint <= '9';
  }

  /**
   * Determine if the code point is an ASCII digit or hexadecimal character.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ASCII digit or hexadecimal character, or {@code false}
   *     otherwise.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  protected static boolean isHexadecimal(int codePoint) {
    return '0' <= codePoint && codePoint <= '9'
        || 'A' <= codePoint && codePoint <= 'F'
        || 'a' <= codePoint && codePoint <= 'f';
  }

  /**
   * Determine if the code point is whitespace.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is whitespace or {@code false} otherwise.
   */
  protected static boolean isWhitespace(int codePoint) {
    return ' ' == codePoint || '\t' == codePoint;
  }

  /**
   * Determine if the code point is a potential newline start.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is whitespace or {@code false} otherwise.
   */
  protected static boolean isNewLineStart(int codePoint) {
    return '\r' == codePoint || '\n' == codePoint;
  }
}
