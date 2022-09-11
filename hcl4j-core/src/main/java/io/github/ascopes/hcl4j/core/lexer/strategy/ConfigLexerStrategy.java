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

package io.github.ascopes.hcl4j.core.lexer.strategy;

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

import io.github.ascopes.hcl4j.core.ex.HclIoException;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.ex.HclSyntaxException;
import io.github.ascopes.hcl4j.core.intern.RawContentBuffer;
import io.github.ascopes.hcl4j.core.lexer.Lexer;
import io.github.ascopes.hcl4j.core.tokens.SimpleToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Default lexer mode used to parse HCL expressions outside templates.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>The bulk of expression handling is dealt with in this class, minus some additional
 * elements like <code>~}</code> that would be handled only in specific contexts.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public final class ConfigLexerStrategy extends CommonLexerStrategy {

  private final Queue<Token> lookAheadQueue;

  public ConfigLexerStrategy(Lexer context) {
    super(context);
    lookAheadQueue = new LinkedList<>();
  }

  @Override
  public Token nextToken() throws HclProcessingException {
    if (!lookAheadQueue.isEmpty()) {
      return lookAheadQueue.remove();
    }

    var nextChar = context.charSource().peek(0);

    if (isWhitespace(nextChar)) {
      return consumeWhitespace();
    }

    if (isNewLineStart(nextChar)) {
      return consumeNewLine();
    }

    if (isIdStart(nextChar)) {
      return consumeIdentifier();
    }

    if (isDigit(nextChar)) {
      return consumeNumber();
    }

    return switch (nextChar) {
      case EOF -> consumeEndOfFile();
      case '+' -> consumePlus();
      case '-' -> consumeMinus();
      case '*' -> consumeAsterisk();
      case '/' -> consumeSlash();
      case '%' -> consumePercent();
      case '&' -> consumeAmpersand();
      case '|' -> consumePipe();
      case '!' -> consumeBang();
      case '=' -> consumeEquals();
      case '<' -> consumeLess();
      case '>' -> consumeGreater();
      case '.' -> consumeDot();
      case '?' -> consumeQuestionMark();
      case ':' -> consumeColon();
      case ',' -> consumeComma();
      case '"' -> consumeQuote();
      case '{' -> consumeLeftBrace();
      case '}' -> consumeRightBrace();
      case '(' -> consumeLeftParenthesis();
      case ')' -> consumeRightParenthesis();
      case '[' -> consumeLeftSquareBracket();
      case ']' -> consumeRightSquareBracket();
      case '#' -> consumeHash();
      case '\'' -> throw syntaxError("Use double quotes for quoted templates", 1);
      default -> throw errorUnrecognisedCharacter();
    };
  }

  private Token consumeNumber() throws HclProcessingException {
    var start = context.charSource().location();
    var buff = new RawContentBuffer();

    // Distinguish between an integer and a real as this enables better handling of numeric data
    // types that may decay if stored in an inaccurate format.
    var real = false;

    tryConsumeIntegerPart(buff);

    if (context.charSource().peek(0) == '.') {
      tryConsumeFractionPart(buff);
      real = true;
    }

    var expPeek = context.charSource().peek(0);

    if (expPeek == 'e' || expPeek == 'E') {
      tryConsumeExponentPart(buff);
      real = true;
    }

    var end = context.charSource().location();
    var type = real ? TokenType.REAL : TokenType.INTEGER;
    return new SimpleToken(type, buff.content(), start, end);
  }

  private void tryConsumeIntegerPart(RawContentBuffer buff) throws HclProcessingException {
    // We always consume as many digits as possible here.
    buff.append(context.charSource().read());

    while (true) {
      var nextChar = context.charSource().peek(0);
      if (!isDigit(nextChar)) {
        break;
      }

      buff.append(nextChar);
      context.charSource().advance(1);
    }
  }

  private void tryConsumeFractionPart(RawContentBuffer buff) throws HclProcessingException {
    // We purposely don't consume the dot if we don't have a digit after it. That enables
    // us to have expressions like 123.name. Might be invalid later, but it gives better error
    // messages.
    if (!isDigit(context.charSource().peek(1))) {
      return;
    }

    // The dot.
    buff.append(context.charSource().read());

    // The digit part.
    tryConsumeIntegerPart(buff);
  }

  private void tryConsumeExponentPart(RawContentBuffer buff) throws HclProcessingException {
    // We purposely don't consume the E if we don't have a + and digit or - and digit after it.
    // This enables us to treat other garbage as separate tokens to give better error messages.

    var secondChar = context.charSource().peek(1);
    var thirdChar = context.charSource().peek(2);

    if ((secondChar == '+' || secondChar == '-') && isDigit(thirdChar)) {
      // E+ or e+ or E- or e-.
      buff.append(context.charSource().readString(2));
      // The digit part.
      tryConsumeIntegerPart(buff);
    } else if (isDigit(secondChar)) {
      // The E or e.
      buff.append(context.charSource().read());
      tryConsumeIntegerPart(buff);
    }
  }

  private Token consumePlus() throws HclProcessingException {
    return newToken(TokenType.PLUS, 1);
  }

  private Token consumeMinus() throws HclProcessingException {
    return newToken(TokenType.MINUS, 1);
  }

  private Token consumeAsterisk() throws HclProcessingException {
    return newToken(TokenType.STAR, 1);
  }

  private Token consumePercent() throws HclProcessingException {
    return newToken(TokenType.MODULO, 1);
  }

  private Token consumeAmpersand() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '&' -> newToken(TokenType.AND, 2);
      default -> throw errorUnknownOperator(2);
    };
  }

  private Token consumeSlash() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '/' -> consumeLineComment();
      case '*' -> consumeInlineComment();
      default -> newToken(TokenType.DIVIDE, 1);
    };
  }

  private Token consumePipe() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '|' -> newToken(TokenType.OR, 2);
      default -> throw errorUnknownOperator(2);
    };
  }

  private Token consumeBang() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.NOT_EQUAL, 2);
      default -> newToken(TokenType.NOT, 1);
    };
  }

  private Token consumeEquals() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.EQUAL, 2);
      case '>' -> newToken(TokenType.FAT_ARROW, 2);
      default -> newToken(TokenType.ASSIGN, 1);
    };
  }

  private Token consumeLess() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '<' -> consumeHeredocAnchor();
      case '=' -> newToken(TokenType.LESS_EQUAL, 2);
      default -> newToken(TokenType.LESS, 1);
    };
  }

  private Token consumeGreater() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.GREATER_EQUAL, 2);
      default -> newToken(TokenType.GREATER, 1);
    };
  }

  private Token consumeDot() throws HclProcessingException {
    return context.charSource().startsWith("...")
        ? newToken(TokenType.ELLIPSIS, 3)
        : newToken(TokenType.DOT, 1);
  }

  private Token consumeQuestionMark() throws HclProcessingException {
    return newToken(TokenType.QUESTION_MARK, 1);
  }

  private Token consumeColon() throws HclProcessingException {
    return newToken(TokenType.COLON, 1);
  }

  private Token consumeComma() throws HclProcessingException {
    return newToken(TokenType.COMMA, 1);
  }

  private Token consumeQuote() throws HclProcessingException {
    var token = newToken(TokenType.OPENING_QUOTE, 1);
    context.pushStrategy(new QuotedTemplateLexerStrategy(context));
    return token;
  }

  private Token consumeLeftBrace() throws HclProcessingException {
    // Push this mode. This can be overridden by a different block of logic for anything
    // subclassing or delegating to this lexer mode (e.g. template lexer modes). We push
    // this mode to enable popping it again afterwards when the block closes.
    context.pushStrategy(this);
    return newToken(TokenType.LEFT_BRACE, 1);
  }

  private Token consumeRightBrace() throws HclProcessingException {
    // Drop out of the current block, whatever that is.
    context.popStrategy();
    return newToken(TokenType.RIGHT_BRACE, 1);
  }

  private Token consumeLeftParenthesis() throws HclProcessingException {
    return newToken(TokenType.LEFT_PAREN, 1);
  }

  private Token consumeRightParenthesis() throws HclProcessingException {
    return newToken(TokenType.RIGHT_PAREN, 1);
  }

  private Token consumeLeftSquareBracket() throws HclProcessingException {
    return newToken(TokenType.LEFT_SQUARE, 1);
  }

  private Token consumeRightSquareBracket() throws HclProcessingException {
    return newToken(TokenType.RIGHT_SQUARE, 1);
  }

  private Token consumeHash() throws HclProcessingException {
    return consumeLineComment();
  }

  private Token consumeLineComment() throws HclProcessingException {
    context.pushStrategy(new LineCommentLexerStrategy(context));

    // We know we either have # or //.
    return context.charSource().peek(0) == '#'
        ? newToken(TokenType.LINE_COMMENT_HASH_START, 1)
        : newToken(TokenType.LINE_COMMENT_SLASH_START, 2);
  }

  private Token consumeInlineComment() throws HclProcessingException {
    context.pushStrategy(new InlineCommentLexerStrategy(context));
    return newToken(TokenType.INLINE_COMMENT_START, 2);
  }

  private Token consumeHeredocAnchor() throws HclProcessingException {
    context.pushStrategy(new HeredocHeaderLexerStrategy(context));
    return newToken(TokenType.HEREDOC_ANCHOR, 2);
  }

  private HclSyntaxException errorUnknownOperator(int length) throws HclIoException {
    return syntaxError("Unknown operator", length);
  }
}
