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

import static io.github.ascopes.hcl4j.core.inputs.HclCharSource.EOF;

import io.github.ascopes.hcl4j.core.ex.HclBadTokenException;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.ex.HclStreamException;
import io.github.ascopes.hcl4j.core.intern.RawContentBuffer;
import io.github.ascopes.hcl4j.core.lexer.HclDefaultLexer;
import io.github.ascopes.hcl4j.core.tokens.HclDefaultToken;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
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
public final class HclConfigLexerStrategy extends HclCommonLexerStrategyBase {

  private final Queue<HclToken> lookAheadQueue;

  public HclConfigLexerStrategy(HclDefaultLexer context) {
    super(context);
    lookAheadQueue = new LinkedList<>();
  }

  @Override
  public HclToken nextToken() throws HclProcessingException {
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

  private HclToken consumeNumber() throws HclProcessingException {
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
    var type = real ? HclTokenType.REAL : HclTokenType.INTEGER;
    return new HclDefaultToken(type, buff.content(), start, end);
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

  private HclToken consumePlus() throws HclProcessingException {
    return newToken(HclTokenType.PLUS, 1);
  }

  private HclToken consumeMinus() throws HclProcessingException {
    return newToken(HclTokenType.MINUS, 1);
  }

  private HclToken consumeAsterisk() throws HclProcessingException {
    return newToken(HclTokenType.STAR, 1);
  }

  private HclToken consumePercent() throws HclProcessingException {
    return newToken(HclTokenType.MODULO, 1);
  }

  private HclToken consumeAmpersand() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '&' -> newToken(HclTokenType.AND, 2);
      default -> throw errorUnknownOperator(2);
    };
  }

  private HclToken consumeSlash() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '/' -> consumeLineComment();
      case '*' -> consumeInlineComment();
      default -> newToken(HclTokenType.DIVIDE, 1);
    };
  }

  private HclToken consumePipe() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '|' -> newToken(HclTokenType.OR, 2);
      default -> throw errorUnknownOperator(2);
    };
  }

  private HclToken consumeBang() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(HclTokenType.NOT_EQUAL, 2);
      default -> newToken(HclTokenType.NOT, 1);
    };
  }

  private HclToken consumeEquals() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(HclTokenType.EQUAL, 2);
      case '>' -> newToken(HclTokenType.FAT_ARROW, 2);
      default -> newToken(HclTokenType.ASSIGN, 1);
    };
  }

  private HclToken consumeLess() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '<' -> consumeHeredocAnchor();
      case '=' -> newToken(HclTokenType.LESS_EQUAL, 2);
      default -> newToken(HclTokenType.LESS, 1);
    };
  }

  private HclToken consumeGreater() throws HclProcessingException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(HclTokenType.GREATER_EQUAL, 2);
      default -> newToken(HclTokenType.GREATER, 1);
    };
  }

  private HclToken consumeDot() throws HclProcessingException {
    return context.charSource().startsWith("...")
        ? newToken(HclTokenType.ELLIPSIS, 3)
        : newToken(HclTokenType.DOT, 1);
  }

  private HclToken consumeQuestionMark() throws HclProcessingException {
    return newToken(HclTokenType.QUESTION_MARK, 1);
  }

  private HclToken consumeColon() throws HclProcessingException {
    return newToken(HclTokenType.COLON, 1);
  }

  private HclToken consumeComma() throws HclProcessingException {
    return newToken(HclTokenType.COMMA, 1);
  }

  private HclToken consumeQuote() throws HclProcessingException {
    var token = newToken(HclTokenType.OPENING_QUOTE, 1);
    context.pushStrategy(new HclQuotedTemplateLexerStrategy(context));
    return token;
  }

  private HclToken consumeLeftBrace() throws HclProcessingException {
    // Push this mode. This can be overridden by a different block of logic for anything
    // subclassing or delegating to this lexer mode (e.g. template lexer modes). We push
    // this mode to enable popping it again afterwards when the block closes.
    context.pushStrategy(this);
    return newToken(HclTokenType.LEFT_BRACE, 1);
  }

  private HclToken consumeRightBrace() throws HclProcessingException {
    // Drop out of the current block, whatever that is.
    context.popStrategy();
    return newToken(HclTokenType.RIGHT_BRACE, 1);
  }

  private HclToken consumeLeftParenthesis() throws HclProcessingException {
    return newToken(HclTokenType.LEFT_PAREN, 1);
  }

  private HclToken consumeRightParenthesis() throws HclProcessingException {
    return newToken(HclTokenType.RIGHT_PAREN, 1);
  }

  private HclToken consumeLeftSquareBracket() throws HclProcessingException {
    return newToken(HclTokenType.LEFT_SQUARE, 1);
  }

  private HclToken consumeRightSquareBracket() throws HclProcessingException {
    return newToken(HclTokenType.RIGHT_SQUARE, 1);
  }

  private HclToken consumeHash() throws HclProcessingException {
    return consumeLineComment();
  }

  private HclToken consumeLineComment() throws HclProcessingException {
    context.pushStrategy(new HclLineCommentLexerStrategy(context));

    // We know we either have # or //.
    return context.charSource().peek(0) == '#'
        ? newToken(HclTokenType.LINE_COMMENT_HASH_START, 1)
        : newToken(HclTokenType.LINE_COMMENT_SLASH_START, 2);
  }

  private HclToken consumeInlineComment() throws HclProcessingException {
    context.pushStrategy(new HclInlineCommentLexerStrategy(context));
    return newToken(HclTokenType.INLINE_COMMENT_START, 2);
  }

  private HclToken consumeHeredocAnchor() throws HclProcessingException {
    context.pushStrategy(new HclHeredocHeaderLexerStrategy(context));
    return newToken(HclTokenType.HEREDOC_ANCHOR, 2);
  }

  @SuppressWarnings("SameParameterValue")
  private HclBadTokenException errorUnknownOperator(int length) throws HclStreamException {
    return syntaxError("Unknown operator", length);
  }
}
