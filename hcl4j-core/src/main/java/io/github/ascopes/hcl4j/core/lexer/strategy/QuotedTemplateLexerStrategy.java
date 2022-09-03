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
import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.lexer.LexerContext;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenErrorMessage;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import io.github.ascopes.hcl4j.core.tokens.impl.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.impl.RawTextToken;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Lexer strategy to handle quoted templates.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This assumes that the heredoc header has already been consumed and the first character will
 * either be the content or the end of the heredoc. Specifically a lexer should have already
 * consumed the header as defined by {@link HeredocHeaderLexerStrategy}.
 *
 * <p>This lexer allows for a small subset of token possibilities, along with some additional
 * behaviours.
 *
 * <ul>
 *   <li>An end-of-file marker will pop the current mode and emit the end of file token.</li>
 *   <li>A new line will be emitted (although it should be considered an error by the parser).</li>
 *   <li>A closing quote will pop the current mode and emit the closing quote token.</li>
 *   <li>An interpolation opening with tilde "<code>$&#123;~</code>" will emit a
 *      {@link TokenType#LEFT_INTERPOLATION_TRIM} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>An interpolation opening without a tilde "<code>$&#123;</code>" will emit a
 *      {@link TokenType#LEFT_INTERPOLATION} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>A directive opening with tilde "<code>%&#123;~</code>" will emit a
 *      {@link TokenType#LEFT_DIRECTIVE_TRIM} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>A directive opening without a tilde "<code>%&#123;</code>" will emit a
 *      {@link TokenType#LEFT_DIRECTIVE} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>Anything else will be collected into a buffer until one of the above cases occurs.
 *      The text will then be emitted as {@link TokenType#RAW_TEXT} as long as at least one
 *      character occurred before the next token of a differing type appeared.
 *      This is with two additional caveats:
 *      <ul>
 *        <li>The literal string {@code &bsol;"} will translate to a literal {@code "}
 *           character.</li>
 *        <li>The literal string {@code &bsol;&bsol;} will translate to a literal {@code &bsol;}
 *           character.</li>
 *        <li>The literal string {@code &bsol;r} will translate to a literal carriage return
 *           character.</li>
 *        <li>The literal string {@code &bsol;n} will translate to a literal line feed
 *           character.</li>
 *        <li>The literal string {@code &bsol;t} will translate to a literal tab character.</li>
 *        <li>The literal string {@code &bsol;uXXXX} where {@code X} is a hexadecimal digit will
 *           translate to a literal character from the basic multilingual plane (BMP) at the
 *           numeric codepoint.</li>
 *        <li>The literal string {@code &bsol;UXXXXXXXX} where {@code X} is a hexadecimal digit will
 *           translate to a literal character from the supplementary plane at the
 *           numeric codepoint.</li>
 *        <li>A left-interpolation marker that is preceded by a dollar "<code>$$&#123;</code>"
 *           will be treated as an escape for a plain-text "<code>$&#123;</code>".</li>
 *        <li>A left-directive marker that is preceded by a percent "<code>%%&#123;</code>"
 *           will be treated as an escape for a plain-text "<code>%&#123;</code>".</li>
 *      </ul>
 *   </li>
 * </ul>
 *
 * @author Ashley Scopes
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public final class QuotedTemplateLexerStrategy extends CommonLexerStrategy {

  private static final int BMP_DIGITS = 4;
  private static final int SUPP_DIGITS = 8;

  private final Queue<Token> errors;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public QuotedTemplateLexerStrategy(LexerContext context) {
    super(context);

    // We can emit encoding errors for bad escape sequences. Do this using a stack to
    // keep things simple.
    errors = new LinkedList<>();
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    if (!errors.isEmpty()) {
      return errors.remove();
    }

    if (context.charSource().startsWith("${")) {
      // Next expression is an interpolation.
      context.pushStrategy(new TemplateExpressionLexerStrategy(context));
      return context.charSource().peek(3) == '~'
          ? newToken(TokenType.LEFT_INTERPOLATION_TRIM, 3)
          : newToken(TokenType.LEFT_INTERPOLATION, 2);
    }

    if (context.charSource().startsWith("%{")) {
      // Next expression is a directive.
      context.pushStrategy(new TemplateExpressionLexerStrategy(context));
      return context.charSource().peek(3) == '~'
          ? newToken(TokenType.LEFT_DIRECTIVE_TRIM, 3)
          : newToken(TokenType.LEFT_DIRECTIVE, 2);
    }

    return switch (context.charSource().peek(0)) {
      case '"' -> {
        context.popStrategy();
        yield newToken(TokenType.QUOTE, 1);
      }
      case EOF -> {
        context.popStrategy();
        yield consumeEndOfFile();
      }
      // Technically invalid, but we allow it here to handle as an error later.
      case '\r', '\n' -> consumeNewLine();
      default -> consumeSomeText();
    };
  }

  private Token consumeSomeText() throws IOException {
    var location = context.charSource().location();
    var raw = new RawTokenBuilder();
    var content = new RawTokenBuilder();

    loop:
    while (true) {
      switch (context.charSource().peek(0)) {
        case EOF, '"' -> {
          break loop;
        }

        case '\\' -> consumeEscape(raw, content);

        case '$' -> {
          if (context.charSource().startsWith("${")) {
            break loop;
          }

          if (context.charSource().startsWith("$${")) {
            raw.append(context.charSource().readString(3));
            content.append("${");
            continue;
          }

          content.append(context.charSource().read());
        }

        case '%' -> {
          if (context.charSource().startsWith("%{")) {
            break loop;
          }

          if (context.charSource().startsWith("%%{")) {
            raw.append(context.charSource().readString(3));
            content.append("%{");
            continue;
          }

          content.append(context.charSource().read());
        }

        default -> {
          var next = context.charSource().read();
          raw.append(next);
          content.append(next);
        }
      }
    }

    return new RawTextToken(raw.raw(), content.raw(), location);
  }

  private void consumeEscape(RawTokenBuilder raw, RawTokenBuilder content) throws IOException {
    switch (context.charSource().peek(1)) {
      case 'n' -> {
        raw.append(context.charSource().readString(2));
        content.append('\n');
      }
      case 'r' -> {
        raw.append(context.charSource().readString(2));
        content.append('\r');
      }
      case 't' -> {
        raw.append(context.charSource().readString(2));
        content.append('\t');
      }
      case '\\' -> {
        raw.append(context.charSource().readString(2));
        content.append('\\');
      }
      case '"' -> {
        raw.append(context.charSource().readString(2));
        content.append('"');
      }
      case 'u', 'U' -> consumeUtf8Escape(raw, content);

      // Anything else is not allowed. EOFs are included in this as it implies
      // we have a dangling backslash.
      default -> {
        // Don't append to the content buffer as it is invalid content.
        var location = context.charSource().location();
        var next = context.charSource().readString(2);
        raw.append(next);
        emitError(new ErrorToken(TokenErrorMessage.MALFORMED_ESCAPE_SEQUENCE, next, location));
      }
    }
  }

  private void consumeUtf8Escape(RawTokenBuilder raw, RawTokenBuilder content) throws IOException {
    var location = context.charSource().location();
    var start = context.charSource().readString(2);
    raw.append(start);

    var digits = new RawTokenBuilder();
    var length = start.equals("\\u") ? BMP_DIGITS : SUPP_DIGITS;
    var i = 0;

    for (; i < length; ++i) {
      var nextChar = context.charSource().peek(i);

      raw.append(nextChar);

      if (nextChar == EOF || !isHexadecimal(nextChar)) {
        emitEscapeError(TokenErrorMessage.MALFORMED_ESCAPE_SEQUENCE, start, digits, location);

        // Syntax error, but handle this best-effort for now.
        break;
      }

      digits.append(nextChar);
    }

    // Skip over whatever we managed to parse from
    // the escape.
    context.charSource().advance(i);

    try {
      content.appendHexCodePoint(digits.raw());
    } catch (IllegalArgumentException ex) {
      // We cannot handle the codepoint. Emit an error at the end of the
      // string and skip this escape sequence for now.
      emitEscapeError(TokenErrorMessage.INVALID_UNICODE_CODE_POINT, start, digits, location);
    }
  }

  private void emitEscapeError(
      TokenErrorMessage errorMessage,
      CharSequence start,
      RawTokenBuilder digits,
      Location location
  ) {
    var err = new ErrorToken(errorMessage, String.join("", start, digits.raw()), location);
    emitError(err);
  }

  private void emitError(ErrorToken error) {
    errors.add(error);
  }
}
