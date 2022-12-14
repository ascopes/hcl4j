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

import static io.github.ascopes.hcl4j.core.inputs.HclCharSource.EOF;

import io.github.ascopes.hcl4j.core.ex.HclBadTokenException;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.intern.RawContentBuffer;
import io.github.ascopes.hcl4j.core.lexer.HclDefaultLexer;
import io.github.ascopes.hcl4j.core.tokens.HclRawTextToken;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;

/**
 * Lexer strategy to handle quoted templates.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This assumes that the heredoc header has already been consumed and the first character will
 * either be the content or the end of the heredoc. Specifically a lexer should have already
 * consumed the header as defined by {@link HclHeredocHeaderLexerStrategy}.
 *
 * <p>This lexer allows for a small subset of token possibilities, along with some additional
 * behaviours.
 *
 * <ul>
 *   <li>An end-of-file marker will pop the current mode and emit the end of file token.</li>
 *   <li>A new line will be emitted (although it should be considered an error by the parser).</li>
 *   <li>A closing quote will pop the current mode and emit the closing quote token.</li>
 *   <li>An interpolation opening with tilde "<code>$&#123;~</code>" will emit a
 *   <li>An interpolation opening "<code>$&#123;</code>" will emit a
 *      {@link HclTokenType#LEFT_INTERPOLATION} token, and a new {@link HclConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>A directive opening "<code>%&#123;</code>" will emit a
 *      {@link HclTokenType#LEFT_DIRECTIVE} token, and a new {@link HclConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>Anything else will be collected into a buffer until one of the above cases occurs.
 *      The valueToken will then be emitted as {@link HclTokenType#RAW_TEXT} as long as at least one
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
 *        <li>A leftToken-interpolation marker that is preceded by a dollar "<code>$$&#123;</code>"
 *           will be treated as an escape for a plain-valueToken "<code>$&#123;</code>".</li>
 *        <li>A leftToken-directive marker that is preceded by a percent "<code>%%&#123;</code>"
 *           will be treated as an escape for a plain-valueToken "<code>%&#123;</code>".</li>
 *      </ul>
 *   </li>
 * </ul>
 *
 * @author Ashley Scopes
 */
public final class HclQuotedTemplateLexerStrategy extends HclCommonLexerStrategyBase {

  private static final int BMP_DIGITS = 4;
  private static final int SUPP_DIGITS = 8;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public HclQuotedTemplateLexerStrategy(HclDefaultLexer context) {
    super(context);
  }

  @Override
  public HclToken nextToken() throws HclProcessingException {
    if (context.charSource().startsWith("${")) {
      // Next expression is an interpolation.
      context.pushStrategy(new HclTemplateExpressionLexerStrategy(context));
      return newToken(HclTokenType.LEFT_INTERPOLATION, 2);
    }

    if (context.charSource().startsWith("%{")) {
      // Next expression is a directive.
      context.pushStrategy(new HclTemplateExpressionLexerStrategy(context));
      return newToken(HclTokenType.LEFT_DIRECTIVE, 2);
    }

    return switch (context.charSource().peek(0)) {
      case '"' -> {
        context.popStrategy();
        yield newToken(HclTokenType.CLOSING_QUOTE, 1);
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

  private HclToken consumeSomeText() throws HclProcessingException {
    var start = context.charSource().location();
    var raw = new RawContentBuffer();
    var content = new RawContentBuffer();

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

    var end = context.charSource().location();
    return new HclRawTextToken(raw.content(), content.content(), start, end);
  }

  private void consumeEscape(RawContentBuffer raw, RawContentBuffer content)
      throws HclProcessingException {

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
        var start = context.charSource().location();
        var next = context.charSource().readString(2);
        raw.append(next);
        var end = context.charSource().location();

        throw new HclBadTokenException(
            context.charSource().name(),
            next,
            start,
            end,
            "Unrecognised string escape sequence"
        );
      }
    }
  }

  private void consumeUtf8Escape(
      RawContentBuffer raw,
      RawContentBuffer content
  ) throws HclProcessingException {

    var start = context.charSource().location();
    var startSequence = context.charSource().readString(2);
    raw.append(startSequence);

    var digits = new RawContentBuffer();
    var length = escapeSequenceLength(startSequence);
    var i = 0;

    for (; i < length; ++i) {
      var nextChar = context.charSource().peek(i);

      raw.append(nextChar);

      if (nextChar == EOF || !isHexadecimal(nextChar)) {
        throw new HclBadTokenException(
            context.charSource().name(),
            raw.content(),
            start,
            context.charSource().location(),
            "Expected " + length + " hexadecimal digits for " + escapeSequenceName(startSequence)
                + " but got " + i
        );
      }

      digits.append(nextChar);
    }

    // Skip over whatever we managed to parse from
    // the escape.
    context.charSource().advance(i);

    try {
      content.appendHexCodePoint(digits.content());
    } catch (IllegalArgumentException ex) {
      // We cannot handle the codepoint. Emit an error at the end of the
      // string and skip this escape sequence for now.
      throw new HclBadTokenException(
          context.charSource().name(),
          raw.content(),
          start,
          context.charSource().location(),
          "Invalid unicode codepoint",
          ex
      );
    }
  }

  private int escapeSequenceLength(CharSequence start) {
    return start.equals("\\u")
        ? BMP_DIGITS : SUPP_DIGITS;
  }

  private String escapeSequenceName(CharSequence start) {
    return start.equals("\\u")
        ? "basic multilingual plane escape sequence"
        : "supplementary plane escape sequence";
  }
}
