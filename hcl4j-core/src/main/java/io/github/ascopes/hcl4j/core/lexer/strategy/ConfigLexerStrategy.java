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

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.lexer.LexerContext;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenErrorMessage;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import io.github.ascopes.hcl4j.core.tokens.impl.SimpleToken;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

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
@API(since = "0.0.1", status = Status.INTERNAL)
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public final class ConfigLexerStrategy extends CommonLexerStrategy {

  private final Queue<Token> lookAheadQueue;

  public ConfigLexerStrategy(LexerContext context) {
    super(context);
    lookAheadQueue = new LinkedList<>();
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
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
      case '\'' -> newError(TokenErrorMessage.USE_DOUBLE_QUOTES, 1);
      default -> consumeUnrecognisedCharacter();
    };
  }

  @CheckReturnValue
  private Token consumeNumber() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder();

    tryConsumeIntegerPart(buff);

    if (context.charSource().peek(0) == '.') {
      tryConsumeFractionPart(buff);
    }

    var expPeek = context.charSource().peek(0);

    if (expPeek == 'e' || expPeek == 'E') {
      tryConsumeExponentPart(buff);
    }

    return new SimpleToken(TokenType.NUMBER, buff.raw(), location);
  }

  private void tryConsumeIntegerPart(RawTokenBuilder buff) throws IOException {
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

  private void tryConsumeFractionPart(RawTokenBuilder buff) throws IOException {
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

  private void tryConsumeExponentPart(RawTokenBuilder buff) throws IOException {
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

  @CheckReturnValue
  private Token consumePlus() throws IOException {
    return newToken(TokenType.PLUS, 1);
  }

  @CheckReturnValue
  private Token consumeMinus() throws IOException {
    return newToken(TokenType.MINUS, 1);
  }

  @CheckReturnValue
  private Token consumeAsterisk() throws IOException {
    return newToken(TokenType.STAR, 1);
  }

  @CheckReturnValue
  private Token consumePercent() throws IOException {
    return newToken(TokenType.MODULO, 1);
  }

  @CheckReturnValue
  private Token consumeAmpersand() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '&' -> newToken(TokenType.AND, 2);
      default -> newError(TokenErrorMessage.UNKNOWN_OPERATOR, 2);
    };
  }

  @CheckReturnValue
  private Token consumeSlash() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '/' -> consumeLineComment();
      case '*' -> consumeInlineComment();
      default -> newToken(TokenType.DIVIDE, 1);
    };
  }

  @CheckReturnValue
  private Token consumePipe() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '|' -> newToken(TokenType.OR, 2);
      default -> newError(TokenErrorMessage.UNKNOWN_OPERATOR, 2);
    };
  }

  @CheckReturnValue
  private Token consumeBang() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.NOT_EQUAL, 2);
      default -> newToken(TokenType.NOT, 1);
    };
  }

  @CheckReturnValue
  private Token consumeEquals() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.EQUAL, 2);
      case '>' -> newToken(TokenType.FAT_ARROW, 2);
      default -> newToken(TokenType.ASSIGN, 1);
    };
  }

  @CheckReturnValue
  private Token consumeLess() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '<' -> consumeHeredocAnchor();
      case '=' -> newToken(TokenType.LESS_EQUAL, 2);
      default -> newToken(TokenType.LESS, 1);
    };
  }

  @CheckReturnValue
  private Token consumeGreater() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.GREATER_EQUAL, 2);
      default -> newToken(TokenType.GREATER, 1);
    };
  }

  @CheckReturnValue
  private Token consumeDot() throws IOException {
    return context.charSource().startsWith("...")
        ? newToken(TokenType.ELLIPSIS, 3)
        : newToken(TokenType.DOT, 1);
  }

  @CheckReturnValue
  private Token consumeQuestionMark() throws IOException {
    return newToken(TokenType.QUESTION_MARK, 1);
  }

  @CheckReturnValue
  private Token consumeColon() throws IOException {
    return newToken(TokenType.COLON, 1);
  }

  @CheckReturnValue
  private Token consumeComma() throws IOException {
    return newToken(TokenType.COMMA, 1);
  }

  @CheckReturnValue
  private Token consumeQuote() throws IOException {
    var token = newToken(TokenType.QUOTE, 1);
    context.pushStrategy(new QuotedTemplateLexerStrategy(context));
    return token;
  }

  @CheckReturnValue
  private Token consumeLeftBrace() throws IOException {
    // Push this mode. This can be overridden by a different block of logic for anything
    // subclassing or delegating to this lexer mode (e.g. template lexer modes). We push
    // this mode to enable popping it again afterwards when the block closes.
    context.pushStrategy(this);
    return newToken(TokenType.LEFT_BRACE, 1);
  }

  @CheckReturnValue
  private Token consumeRightBrace() throws IOException {
    // Drop out of the current block, whatever that is.
    context.popStrategy();
    return newToken(TokenType.RIGHT_BRACE, 1);
  }

  @CheckReturnValue
  private Token consumeLeftParenthesis() throws IOException {
    return newToken(TokenType.LEFT_PAREN, 1);
  }

  @CheckReturnValue
  private Token consumeRightParenthesis() throws IOException {
    return newToken(TokenType.RIGHT_PAREN, 1);
  }

  @CheckReturnValue
  private Token consumeLeftSquareBracket() throws IOException {
    return newToken(TokenType.LEFT_SQUARE, 1);
  }

  @CheckReturnValue
  private Token consumeRightSquareBracket() throws IOException {
    return newToken(TokenType.RIGHT_SQUARE, 1);
  }

  @CheckReturnValue
  private Token consumeHash() throws IOException {
    return consumeLineComment();
  }

  @CheckReturnValue
  private Token consumeLineComment() throws IOException {
    context.pushStrategy(new LineCommentLexerStrategy(context));

    // We know we either have # or //.
    return context.charSource().peek(0) == '#'
        ? newToken(TokenType.LINE_COMMENT_HASH_START, 1)
        : newToken(TokenType.LINE_COMMENT_SLASH_START, 2);
  }

  @CheckReturnValue
  private Token consumeInlineComment() throws IOException {
    context.pushStrategy(new InlineCommentLexerStrategy(context));
    return newToken(TokenType.INLINE_COMMENT_START, 2);
  }

  @CheckReturnValue
  private Token consumeHeredocAnchor() throws IOException {
    context.pushStrategy(new HeredocHeaderLexerStrategy(context));
    return newToken(TokenType.HEREDOC_ANCHOR, 2);
  }
}
