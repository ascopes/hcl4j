/*
 * Copyright (C) 2022 Ashley Scopes
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

package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.tokens.EofToken;
import io.github.ascopes.hcl4j.core.tokens.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.SimpleToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenErrorMessage;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Abstract implementation of a lexer strategy with some common behaviours pre-implemented.
 *
 * <p>LexerContext strategies should usually derive from this class for simplicity.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public abstract class CommonLexerStrategy implements LexerStrategy {

  protected final LexerContext context;

  /**
   * Initialize this strategy.
   *
   * @param context the lexer context to use.
   */
  protected CommonLexerStrategy(LexerContext context) {
    this.context = context;
  }

  /**
   * Construct a token from the given number of characters.
   *
   * @param type   the type of token to use.
   * @param length the character count to read.
   * @return the token.
   * @throws IOException if an {@link IOException} occurred internally.
   */
  @CheckReturnValue
  protected Token newToken(TokenType type, int length) throws IOException {
    var location = context.charSource().location();
    var raw = context.charSource().readString(length);
    assert raw.length() == length : "EOF reached prematurely, missing check occurred elsewhere";
    return new SimpleToken(type, raw, location);
  }

  /**
   * Construct an error from the given number of characters.
   *
   * @param error  the error to use.
   * @param length the character count to read.
   * @return the error token.
   * @throws IOException if an {@link IOException} occurred internally.
   */
  @CheckReturnValue
  protected Token newError(TokenErrorMessage error, int length) throws IOException {
    var location = context.charSource().location();
    var raw = context.charSource().readString(length);
    assert raw.length() == length : "EOF reached prematurely, missing check occurred elsewhere";
    return new ErrorToken(error, raw, location);
  }

  /**
   * Consume an end-of-file token.
   *
   * @return the token.
   */
  @CheckReturnValue
  protected Token consumeEndOfFile() {
    return new EofToken(context.charSource().location());
  }

  /**
   * Consume an unknown character and emit it as an error.
   *
   * @return the error.
   * @throws IOException if an {@link IOException} occurs during parsing.
   */
  @CheckReturnValue
  protected Token consumeUnrecognisedCharacter() throws IOException {
    return newError(TokenErrorMessage.UNRECOGNISED_CHAR, 1);
  }

  /**
   * Consume an identifier, given the assumption that the first character is an ID_START.
   *
   * @return the token.
   * @throws IOException if an {@link IOException} occurs during parsing.
   */
  @CheckReturnValue
  protected Token consumeIdentifier() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder()
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

    return new SimpleToken(TokenType.IDENTIFIER, buff.raw(), location);
  }

  /**
   * Consume some whitespace, given the assumption that the current character is whitespace.
   *
   * @return the token.
   * @throws IOException if an {@link IOException} occurs during parsing.
   */
  @CheckReturnValue
  protected Token consumeWhitespace() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder()
        .append(context.charSource().read());

    while (true) {
      var nextChar = context.charSource().peek(0);

      if (nextChar != ' ' && nextChar != '\t') {
        break;
      }

      buff.append(nextChar);
      context.charSource().advance(1);
    }

    return new SimpleToken(TokenType.WHITESPACE, buff.raw(), location);
  }

  /**
   * Consume some whitespace, given the assumption that the current character is a newline start. or
   * a line feed.
   *
   * @return the token.
   * @throws IOException if an {@link IOException} occurs during parsing.
   */
  @CheckReturnValue
  protected Token consumeNewLine() throws IOException {
    return switch (context.charSource().peek(0)) {
      case '\r' -> context.charSource().peek(1) == '\n'
          ? newToken(TokenType.NEW_LINE, 2)
          : newError(TokenErrorMessage.UNRECOGNISED_CHAR, 1);
      case '\n' -> newToken(TokenType.NEW_LINE, 1);
      default -> newError(TokenErrorMessage.UNRECOGNISED_CHAR, 1);
    };
  }

  /**
   * Determine if the code point is an HCL ID_START character.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ID_START character, or {@code false} otherwise.
   */
  @CheckReturnValue
  protected static boolean isIdStart(int codePoint) {
    return Character.isUnicodeIdentifierStart(codePoint);
  }

  /**
   * Determine if the code point is an HCL ID_CONTINUE character.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ID_CONTINUE character, or {@code false} otherwise.
   */
  @CheckReturnValue
  protected static boolean isIdContinue(int codePoint) {
    return Character.isUnicodeIdentifierPart(codePoint) || codePoint == '-';
  }

  /**
   * Determine if the code point is an ASCII digit.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is an ASCII digit, or {@code false} otherwise.
   */
  @CheckReturnValue
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
  @CheckReturnValue
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
  @CheckReturnValue
  protected static boolean isWhitespace(int codePoint) {
    return ' ' == codePoint || '\t' == codePoint;
  }

  /**
   * Determine if the code point is a potential newline start.
   *
   * @param codePoint the code point to check.
   * @return {@code true} if it is whitespace or {@code false} otherwise.
   */
  @CheckReturnValue
  protected static boolean isNewLineStart(int codePoint) {
    return '\r' == codePoint || '\n' == codePoint;
  }
}
