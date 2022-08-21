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

import static io.github.ascopes.hcl4j.core.utils.HclTextUtils.join;

import io.github.ascopes.hcl4j.core.tokens.EofToken;
import io.github.ascopes.hcl4j.core.tokens.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.ErrorToken.ErrorType;
import io.github.ascopes.hcl4j.core.tokens.HereDocAnchorToken;
import io.github.ascopes.hcl4j.core.tokens.IdentifierToken;
import io.github.ascopes.hcl4j.core.tokens.NumberToken;
import io.github.ascopes.hcl4j.core.tokens.OperatorToken;
import io.github.ascopes.hcl4j.core.tokens.OperatorToken.Operator;
import io.github.ascopes.hcl4j.core.tokens.QuotedAnchor;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.utils.HclTextUtils;
import io.github.ascopes.hcl4j.core.utils.Nullable;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Lexer mode for regular parsing outside of special syntax blocks.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class DefaultLexerMode implements LexerMode {

  private final CharSource source;
  private final LexerModeControl lexer;

  /**
   * Initialize the default lexer mode.
   *
   * @param source the source.
   * @param lexer  the lexer.
   */
  public DefaultLexerMode(CharSource source, LexerModeControl lexer) {
    this.source = source;
    this.lexer = lexer;
  }

  /**
   * Parse the next token.
   *
   * <pre><code>
   *   DefaultToken ::= Eof
   *                  | Numeric
   *                  | Identifier
   *                  | QuotedStart
   *                  | HereDocAnchor
   *                  | Operator
   *                  | Error
   *                  ;
   * </code></pre>
   *
   * <p>If the stream is at the end of the file, the lexer mode will be popped before returning.
   *
   * @return the next token.
   * @throws IOException if a failure occurs reading the input stream.
   */
  @Override
  public Token nextToken() throws IOException {
    var wsError = skipWhitespace();

    if (wsError != null) {
      return wsError;
    }

    var location = source.createLocation();
    var nextChar = source.eat();

    if (HclTextUtils.isEof(nextChar)) {
      return handleEof(location);
    }

    if (HclTextUtils.isNumeric(nextChar)) {
      return handleNumeric(location, nextChar);
    }

    if (HclTextUtils.isIdStart(nextChar)) {
      return handleIdentifier(location, nextChar);
    }

    if (nextChar == '"') {
      return handleQuotedStart(location, nextChar);
    }

    if (nextChar == '<' && source.peek() == '<') {
      return handleHereDocAnchor(location, nextChar, source.eat());
    }

    return handleOperatorOrUnknown(location, nextChar);
  }

  /**
   * Parse an EOF token.
   *
   * <pre><code>
   *   Eof ::= ;
   * </code></pre>
   *
   * @param location the current location.
   * @return the parsed token.
   */
  private Token handleEof(Location location) {
    lexer.popMode();
    return new EofToken(location);
  }

  /**
   * Parse a numeric token, assuming the current character is already a known digit.
   *
   * <pre><code>
   *   Numeric      ::= Digits ( DecimalPoint Digits )? ( Exp Sign? Digits )? ;
   *   Digits       ::= [0-9]+ ;
   *   DecimalPoint ::= '.' ;
   *   Exp          ::= [Ee] ;
   *   Sign         ::= [+-] ;
   * </code></pre>
   *
   * @param location  the current location.
   * @param firstChar the first character.
   * @return the parsed token.
   * @throws IOException if a failure occurs reading the input stream.
   */
  private Token handleNumeric(Location location, int firstChar) throws IOException {
    var raw = new StringBuilder()
        .append((char) firstChar);

    while (HclTextUtils.isNumeric(source.peek())) {
      raw.append((char) source.eat());
    }

    if (source.peek() == '.') {
      raw.append(source.eat());

      if (!HclTextUtils.isNumeric(source.peek())) {
        return new ErrorToken(
            // Use the current location to improve the error message accuracy.
            source.createLocation(),
            ErrorType.MALFORMED_NUMBER_LITERAL,
            "Expected at least one digit after the decimal point in a number literal",
            raw.toString()
        );
      }

      while (HclTextUtils.isNumeric(source.peek())) {
        raw.append((char) source.eat());
      }
    }

    if (source.peek() == 'e' || source.peek() == 'E') {
      raw.append(source.eat());

      if (source.peek() == '+' || source.peek() == '-') {
        raw.append(source.eat());
      }

      if (!HclTextUtils.isNumeric(source.peek())) {
        return new ErrorToken(
            // Use the current location to improve the error message accuracy.
            source.createLocation(),
            ErrorType.MALFORMED_NUMBER_LITERAL,
            "Expected at least one digit after the exponent in a number literal",
            raw.toString()
        );
      }

      while (HclTextUtils.isNumeric(source.peek())) {
        raw.append((char) source.eat());
      }
    }

    var rawContent = raw.toString();
    var decimal = new BigDecimal(rawContent);
    return new NumberToken(location, decimal, rawContent);
  }

  /**
   * Parse an identifier token, assuming the current character is already a known {@code IdStart}.
   *
   * <pre><code>
   *   Identifier   ::= IdStart ( IdContinue | Hyphen )* ;
   *   Hyphen       ::= '-' ;
   * </code></pre>
   *
   * <p>{@code IdStart} is a UTF-8 {@code ID_START} character as defined in the Unicode standard.
   * {@code IdContinue} is a UTF-8 {@code ID_CONTINUE} character as defined in the Unicode
   * standard.
   *
   * @param location  the current location.
   * @param firstChar the first character.
   * @return the parsed token.
   * @throws IOException if a failure occurs reading the input stream.
   */
  private Token handleIdentifier(Location location, int firstChar) throws IOException {
    var raw = new StringBuilder()
        .append((char) firstChar);

    while (HclTextUtils.isIdContinue(source.peek())) {
      raw.append((char) source.eat());
    }

    return new IdentifierToken(location, raw.toString());
  }

  /**
   * Parse an identifier token, assuming the current character is already a known quote character.
   *
   * <pre><code>
   *   QuotedStart ::= '"' ;
   * </code></pre>
   *
   * @param location  the current location.
   * @param firstChar the first character.
   * @return the parsed token.
   */
  private Token handleQuotedStart(Location location, int firstChar) {
    var startToken = new QuotedAnchor(location, firstChar);
    lexer.pushMode(new QuotedLexerMode(source, lexer));
    return startToken;
  }

  /**
   * Parse a heredoc anchor token, assuming the first two characters are already known, consumed,
   * and validated.
   *
   * <pre><code>
   *    HereDocAnchor ::= LtLt IndentSign? Identifier NewLine ;
   *    LtLt ::= '<<' ;
   *    IndentSign ::= '-' ;
   *    NewLine ::= '\r\n' | '\n' ;
   * </code></pre>
   *
   * @param location   the current location.
   * @param firstChar  the first character, always a '{@code &lt;}'.
   * @param secondChar the second character, always a '{@code &lt;}'.
   * @return the parsed token.
   * @throws IOException if a failure occurs reading the input stream.
   */
  private Token handleHereDocAnchor(
      Location location,
      int firstChar,
      int secondChar
  ) throws IOException {
    var raw = new StringBuilder()
        .append((char) firstChar)
        .append((char) secondChar);

    boolean indented;

    if (source.peek() == '-') {
      indented = true;
      raw.append((char) source.eat());
    } else {
      indented = false;
    }

    // Take the identifier.
    if (!HclTextUtils.isIdStart(source.peek())) {
      return new ErrorToken(
          // Use the current location to improve the error message accuracy.
          source.createLocation(),
          ErrorType.MALFORMED_CLOSING_HEREDOC_ANCHOR,
          "Heredoc anchor is missing a valid identifier",
          raw.toString()
      );
    }

    var id = new StringBuilder().append((char) source.eat());
    while (HclTextUtils.isIdContinue(source.peek())) {
      id.append((char) source.eat());
    }
    raw.append(id);

    // Take the newline.
    if (source.peek() == HclTextUtils.CR) {
      raw.append((char) source.eat());
    }

    if (source.peek() == HclTextUtils.LF) {
      raw.append((char) source.eat());
    } else {
      return new ErrorToken(
          // Use the current location to improve the error message accuracy.
          source.createLocation(),
          ErrorType.MALFORMED_OPENING_HEREDOC_ANCHOR,
          "Expected CRLF or LF new line after heredoc anchor declaration",
          raw.toString()
      );
    }

    var anchorToken = new HereDocAnchorToken(location, id.toString(), indented, raw.toString());
    lexer.pushMode(new HereDocLexerMode(source, lexer, anchorToken));
    return anchorToken;
  }

  /**
   * Parse an arbitrary operator not covered by any other rules, or failing that, return an error
   * token instead.
   *
   * @param location  the current location.
   * @param firstChar the first character.
   * @return the parsed token.
   * @throws IOException if a failure occurs reading the input stream.
   */
  private Token handleOperatorOrUnknown(Location location, int firstChar) throws IOException {
    switch (firstChar) {
      case '+':
        return new OperatorToken(location, Operator.PLUS, firstChar);

      case '-':
        return new OperatorToken(location, Operator.MINUS, firstChar);

      case '*':
        return new OperatorToken(location, Operator.MULTIPLY, firstChar);

      case '/':
        return new OperatorToken(location, Operator.DIVIDE, firstChar);

      case '%':
        return new OperatorToken(location, Operator.MODULO, firstChar);

      case '&':
        return source.peek() == '&'
            ? new OperatorToken(location, Operator.AND, firstChar, source.eat())
            : unknownOperator(location, firstChar, source.eat());

      case '|':
        return source.peek() == '|'
            ? new OperatorToken(location, Operator.OR, firstChar, source.eat())
            : unknownOperator(location, firstChar, source.eat());

      case '!':
        return source.peek() == '='
            ? new OperatorToken(location, Operator.NOT_EQUAL, firstChar, source.eat())
            : new OperatorToken(location, Operator.NOT, firstChar);

      case '=':
        switch (source.peek()) {
          case '=':
            return new OperatorToken(location, Operator.EQUAL, firstChar, source.eat());
          case '>':
            return new OperatorToken(location, Operator.FAT_ARROW, firstChar, source.eat());
          default:
            return new OperatorToken(location, Operator.ASSIGN, firstChar);
        }

      case '<':
        return source.peek() == '='
            ? new OperatorToken(location, Operator.LESS_EQUAL, firstChar, source.eat())
            : new OperatorToken(location, Operator.LESS, firstChar);

      case '>':
        return source.peek() == '='
            ? new OperatorToken(location, Operator.GREATER_EQUAL, firstChar, source.eat())
            : new OperatorToken(location, Operator.GREATER, firstChar);

      case '(':
        return new OperatorToken(location, Operator.LEFT_PAREN, firstChar);

      case ')':
        return new OperatorToken(location, Operator.RIGHT_PAREN, firstChar);

      case '[':
        return new OperatorToken(location, Operator.LEFT_SQUARE, firstChar);

      case ']':
        return new OperatorToken(location, Operator.RIGHT_SQUARE, firstChar);

      case '{':
        return new OperatorToken(location, Operator.LEFT_BRACE, firstChar);

      case '}':
        return new OperatorToken(location, Operator.RIGHT_BRACE, firstChar);

      case '?':
        return new OperatorToken(location, Operator.QUESTION_MARK, firstChar);

      case ':':
        return new OperatorToken(location, Operator.COLON, firstChar);

      case '.':
        return source.peek() == '.'
            ? handleEllipsisOperator(location, firstChar, source.eat())
            : new OperatorToken(location, Operator.DOT, firstChar);

      case ',':
        return new OperatorToken(location, Operator.COMMA, firstChar);

      default:
        return new ErrorToken(
            location,
            ErrorType.UNEXPECTED_CHARACTER,
            "Unexpected character '" + (char) firstChar + "'",
            firstChar
        );
    }
  }

  /**
   * Parse the ellipsis {@code ...} operator, assuming the first two characters are already known,
   * consumed, and validated.
   *
   * @param location   the location.
   * @param firstChar  the first character, always '{@code .}'.
   * @param secondChar the second character, always '{@code .}'.
   * @return the ellipsis operator token, or an error token.
   * @throws IOException if a failure occurred reading the input stream.
   */
  private Token handleEllipsisOperator(
      Location location,
      int firstChar,
      int secondChar
  ) throws IOException {
    if (source.peek() == '.') {
      return new OperatorToken(location, Operator.ELLIPSIS, firstChar, secondChar, source.eat());
    }

    return unknownOperator(location, firstChar, secondChar);
  }

  /**
   * Report an unknown operator occurred.
   *
   * @param location the location.
   * @param chars    the characters making up the unknown operator.
   * @return the error token.
   */
  private Token unknownOperator(Location location, int... chars) {
    var operator = join(chars);
    var detail = "Unknown operator '" + operator + "'";
    return new ErrorToken(location, ErrorType.UNKNOWN_OPERATOR, detail, operator);
  }

  /**
   * Skip any whitespace that occurs in the lexer.
   *
   * @return the error token if a whitespace error occurs, or {@code null} if no issues occurred.
   * @throws IOException if a failure occurs reading the input stream.
   */
  @Nullable
  private ErrorToken skipWhitespace() throws IOException {
    while (true) {
      switch (source.peek()) {
        case ' ':
        case HclTextUtils.TAB:
        case HclTextUtils.LF:
          source.skip(1);
          break;

        case HclTextUtils.CR: {
          var location = source.createLocation();
          var cr = source.eat();

          if (source.peek() != HclTextUtils.LF) {
            return new ErrorToken(
                // Use the current location to improve the error message accuracy.
                location,
                ErrorType.MALFORMED_NEWLINE_SEQUENCE,
                "Expected CRLF or LF newlines, found CR",
                cr, source.eat()
            );
          }

          break;
        }

        default:
          return null;
      }
    }
  }
}
