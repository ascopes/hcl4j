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
import io.github.ascopes.hcl4j.core.tokens.HereDocAnchorToken;
import io.github.ascopes.hcl4j.core.tokens.IdentifierToken;
import io.github.ascopes.hcl4j.core.tokens.OperatorToken;
import io.github.ascopes.hcl4j.core.tokens.OperatorToken.Operator;
import io.github.ascopes.hcl4j.core.tokens.RawTextToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.utils.HclTextUtils;
import java.io.IOException;

/**
 * Lexer mode for handling heredoc contents.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HereDocLexerMode implements LexerMode {

  private final CharSource source;
  private final LexerModeControl lexer;
  private final String closingIdentifier;

  public HereDocLexerMode(
      CharSource source,
      LexerModeControl lexer,
      HereDocAnchorToken anchorToken
  ) {
    this.source = source;
    this.lexer = lexer;
    closingIdentifier = anchorToken.getIdentifier();
  }

  @Override
  public Token nextToken() throws IOException {
    var location = source.createLocation();
    var builder = new StringBuilder();

    while (!isClosingIdentifierAhead() && source.peek() != HclTextUtils.EOF) {
      var nextChar = source.eat();

      // ${ or $${
      // The former is a marker for an interpolation expression, the latter is an escape sequence
      // for ${
      if (nextChar == '$') {
        if (source.peek(3).equals("${")) {
          // Just an escape, treat $${ as ${
          builder
              .append((char) source.eat())
              .append((char) source.eat());
          continue;
        }

        if (source.peek() == '{') {
          // If we didn't parse any other content yet, we should consume the operator now.
          return builder.length() == 0
              ? handleInterpStart(location, nextChar)
              : new RawTextToken(location, builder.toString());
        }
      }

      // %{ or %%{
      // The former is a marker for an interpolation expression, the latter is an escape sequence
      // for %{
      if (nextChar == '%') {
        if (source.peek(2).equals("%{")) {
          // Just an escape, treat %%{ as %{
          builder
              .append(nextChar)
              .append((char) source.eat())
              .append((char) source.eat());
          continue;
        }

        if (source.peek() == '{') {
          // If we didn't parse any other content yet, we should consume the operator now.
          return builder.length() == 0
              ? handleControlStart(location, nextChar)
              : new RawTextToken(location, builder.toString());
        }
      }

      // Must be some other character instead.
      builder.append((char) nextChar);
    }

    // If we read any characters into the buffer, we should first emit those as a raw text fragment
    // Otherwise, it is fine to parse the next token and change the lexer mode.
    if (builder.length() == 0) {
      // If the next character is an EOF, we got a premature EOF and the document is malformed.
      // Other
      if (source.peek() == HclTextUtils.EOF) {
        return handleUnexpectedEof();
      } else {
        return handleClosingIdentifier(location);
      }
    } else {
      return new RawTextToken(location, builder.toString());
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

  private boolean isClosingIdentifierAhead() throws IOException {
    return source.peek(closingIdentifier.length()).equals(closingIdentifier);
  }

  private Token handleClosingIdentifier(Location location) throws IOException {
    // We know the identifier is already present, so just skip that.
    source.skip(closingIdentifier.length());

    // Expect either \r\n or \n
    if (source.peek() == '\r') {
      source.skip(1);
    }

    if (source.peek() != '\n') {
      return new ErrorToken(
          source.createLocation(),
          ErrorType.MALFORMED_CLOSING_HEREDOC_ANCHOR,
          "Expected CRLF or LF after closing heredoc anchor",
          source.peek()
      );
    }

    source.eat();
    var token = new IdentifierToken(location, closingIdentifier);
    lexer.popMode();
    return token;
  }

  private Token handleUnexpectedEof() {
    var token = new ErrorToken(
        source.createLocation(),
        ErrorType.UNEXPECTED_END_OF_FILE,
        "Unexpected end-of-file reached before heredoc was closed"
    );

    // Don't continue to parse a heredoc. Let the lexer terminate.
    lexer.popMode();
    return token;
  }
}
