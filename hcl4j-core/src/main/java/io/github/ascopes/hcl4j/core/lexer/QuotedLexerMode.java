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

import io.github.ascopes.hcl4j.core.tokens.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.ErrorToken.ErrorType;
import io.github.ascopes.hcl4j.core.tokens.OperatorToken;
import io.github.ascopes.hcl4j.core.tokens.OperatorToken.Operator;
import io.github.ascopes.hcl4j.core.tokens.QuotedAnchor;
import io.github.ascopes.hcl4j.core.tokens.RawTextToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.utils.HclTextUtils;
import java.io.IOException;

/**
 * Lexer mode for handling quoted templates.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class QuotedLexerMode implements LexerMode {

  private final CharSource source;
  private final LexerModeControl lexer;

  public QuotedLexerMode(CharSource source, LexerModeControl lexer) {
    this.source = source;
    this.lexer = lexer;
  }

  @Override
  public Token nextToken() throws IOException {
    var location = source.createLocation();
    var content = new StringBuilder();

    while (source.peek() != '"' && source.peek() != HclTextUtils.EOF) {
      var nextChar = source.eat();

      // ${ or $${
      // The former is a marker for an interpolation expression, the latter is an escape sequence
      // for ${
      if (nextChar == '$') {
        if (source.peek(3).equals("${")) {
          // Just an escape, treat $${ as ${
          content
              .append((char) source.eat())
              .append((char) source.eat());
          continue;
        }

        if (source.peek() == '{') {
          // If we didn't parse any other content yet, we should consume the operator now.
          return content.length() == 0
              ? handleInterpStart(location, nextChar)
              : new RawTextToken(location, content.toString());
        }
      }

      // %{ or %%{
      // The former is a marker for an interpolation expression, the latter is an escape sequence
      // for %{
      if (nextChar == '%') {
        if (source.peek(2).equals("%{")) {
          // Just an escape, treat %%{ as %{
          content
              .append(nextChar)
              .append((char) source.eat())
              .append((char) source.eat());
          continue;
        }

        if (source.peek() == '{') {
          // If we didn't parse any other content yet, we should consume the operator now.
          return content.length() == 0
              ? handleControlStart(location, nextChar)
              : new RawTextToken(location, content.toString());
        }
      }

      if (nextChar == '\\') {
        switch (source.peek()) {
          case 'n':
            content.append((char) HclTextUtils.LF);
            source.eat();
            break;
          case 'r':
            content.append((char) HclTextUtils.CR);
            source.eat();
            break;
          case 't':
            content.append((char) HclTextUtils.TAB);
            source.eat();
            break;
          case '\\':
            content.append('\\');
            source.eat();
            break;
          case '\"':
            content.append('\"');
            source.eat();
            break;

          case 'u': {
            // Basic multilingual plane literal, \uA2B4
            source.eat();

            var bmp = new StringBuilder();

            for (var i = 0; i < 4; ++i) {
              var next = source.eat();
              bmp.append(next);
              if (!HclTextUtils.isHexadecimal(next)) {
                return handleMalformedBmpSequence("\\u" + bmp);
              }
            }

            content.append((char) Integer.parseInt(bmp.toString(), 16));
            break;
          }

          case 'U': {
            // Supplementary plane literal, \UA2B4C6D8
            source.eat();

            var sup = new StringBuilder();

            for (var i = 0; i < 8; ++i) {
              var next = source.eat();
              sup.append(next);
              if (!HclTextUtils.isHexadecimal(next)) {
                return handleMalformedSupSequence("\\U" + sup);
              }
            }

            content.append((char) Integer.parseInt(sup.toString(), 16));
            break;
          }
        }
      }

      content.append((char) nextChar);
    }

    // If we read any characters into the buffer, we should first emit those as a raw text fragment
    // Otherwise, it is fine to parse the next token and change the lexer mode.
    if (content.length() == 0) {
      // If the next character is an EOF, we got a premature EOF and the document is malformed.
      // Other
      if (source.peek() == HclTextUtils.EOF) {
        return handleUnexpectedEof();
      } else {
        return handleClosingQuote(location);
      }
    } else {
      return new RawTextToken(location, content.toString());
    }
  }

  private Token handleInterpStart(Location location, int firstChar) throws IOException {
    var token = new OperatorToken(location, Operator.INTERP_START, firstChar, source.eat());
    lexer.pushMode(new InterpolationLexerMode(source, lexer));
    return token;
  }

  private Token handleControlStart(Location location, int firstChar) throws IOException {
    var token = new OperatorToken(location, Operator.CONTROL_START, firstChar, source.eat());
    lexer.pushMode(new ControlLexerMode(source, lexer));
    return token;
  }

  private Token handleClosingQuote(Location location) throws IOException {
    // We already know the character is correct, so just consume it.
    var token = new QuotedAnchor(location, source.eat());
    lexer.popMode();
    return token;
  }

  private Token handleUnexpectedEof() {
    var token = new ErrorToken(
        source.createLocation(),
        ErrorType.UNEXPECTED_END_OF_FILE,
        "Unexpected end-of-file reached before quoted template was closed"
    );

    // Don't continue to parse a heredoc. Let the lexer terminate.
    lexer.popMode();
    return token;
  }

  private Token handleMalformedBmpSequence(String raw) {
    return new ErrorToken(
        source.createLocation(),
        ErrorType.MALFORMED_UNICODE_ESCAPE_SEQUENCE,
        "Malformed unicode escape sequence. Expected \\uXXXX where X is a hexadecimal digit",
        raw

    );
  }

  private Token handleMalformedSupSequence(String raw) {
    return new ErrorToken(
        source.createLocation(),
        ErrorType.MALFORMED_UNICODE_ESCAPE_SEQUENCE,
        "Malformed unicode escape sequence. Expected \\UXXXXXXXX where X is a hexadecimal digit",
        raw
    );
  }
}
