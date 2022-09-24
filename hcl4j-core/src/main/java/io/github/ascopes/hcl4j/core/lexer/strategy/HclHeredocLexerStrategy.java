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

import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.intern.RawContentBuffer;
import io.github.ascopes.hcl4j.core.lexer.HclDefaultLexer;
import io.github.ascopes.hcl4j.core.tokens.HclRawTextToken;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;

/**
 * Lexer strategy for tokenizing content within a heredoc directive until the heredoc is
 * terminated.
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
 *   <li>An end-of-file marker will pop the current mode.</li>
 *   <li>If the identifier matches the provided identifier given during construction,
 *      followed by a new line character, then we consider that to be the end of the heredoc.
 *      This lexer strategy will be popped and the identifier up to but not including the new line
 *      will be emitted as an {@link HclTokenType#IDENTIFIER}.</li>
 *   <li>An interpolation opening will "<code>$&#123;</code>" will emit a
 *      {@link HclTokenType#LEFT_INTERPOLATION} token, and a new
 *      {@link HclTemplateExpressionLexerStrategy} will be pushed onto the lexer strategy
 *      stack.</li>
 *   <li>A directive opening with tilde "<code>%&#123;~</code>" will emit a
 *      {@link HclTokenType#LEFT_DIRECTIVE} token, and a new
 *      {@link HclTemplateExpressionLexerStrategy} will be pushed onto the lexer strategy
 *      stack.</li>
 *   <li>Anything else will be collected into a buffer until one of the above cases occurs.
 *      The valueToken will then be emitted as {@link HclTokenType#RAW_TEXT} as long as at least one
 *      character occurred before the next token of a differing type appeared.
 *      This is with two additional caveats:
 *      <ul>
 *        <li>A leftToken-interpolation marker that is preceded by a dollar "<code>$$&#123;</code>"
 *           will be treated as an escape for a plain-valueToken "<code>$&#123;</code>".</li>
 *        <li>A leftToken-directive marker that is preceded by a percent "<code>%%&#123;</code>"
 *           will be treated as an escape for a plain-valueToken "<code>%&#123;</code>".</li>
 *      </ul>
 *   </li>
 * </ul>
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclHeredocLexerStrategy extends HclCommonLexerStrategyBase {

  private final CharSequence identifier;

  /**
   * Initialize the strategy.
   *
   * @param context    the context to use.
   * @param identifier the closing identifier to use.
   */
  public HclHeredocLexerStrategy(HclDefaultLexer context, CharSequence identifier) {
    super(context);
    this.identifier = identifier;
  }

  @Override
  public HclToken nextToken() throws HclProcessingException {
    if (isClosingIdentifierAhead()) {
      // Time to pop the lexer mode, we have reached the end of the heredoc.
      context.popStrategy();
      return consumeIdentifier();
    }

    if (context.charSource().peek(0) == EOF) {
      // EOF. Shouldn't have happened. CommonParser should deal with this.
      context.popStrategy();
      return consumeEndOfFile();
    }

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

    return consumeSomeText();
  }

  @Nullable
  private boolean isClosingIdentifierAhead() throws HclProcessingException {
    var i = 0;
    for (; i < identifier.length(); ++i) {
      if (context.charSource().peek(i) != identifier.charAt(i)) {
        // Not a match.
        return false;
      }
    }

    // Identifier should always have a newline after it. If we have anything else, including EOF,
    // then do not treat it as an identifier.
    return switch (context.charSource().peek(i)) {
      case '\r', '\n' -> true;
      default -> false;
    };
  }

  private HclToken consumeSomeText() throws HclProcessingException {
    var start = context.charSource().location();
    var raw = new RawContentBuffer();
    var content = new RawContentBuffer();

    loop:
    while (true) {
      switch (context.charSource().peek(0)) {
        case EOF -> {
          break loop;
        }
        case '$' -> {
          if (context.charSource().startsWith("${")) {
            break loop;
          }

          if (context.charSource().startsWith("$${")) {
            raw.append(context.charSource().readString(3));
            content.append("${");
            continue;
          }
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
        }
      }

      if (isClosingIdentifierAhead()) {
        break;
      }

      var next = context.charSource().read();

      content.append(next);
      raw.append(next);
    }

    var end = context.charSource().location();

    return new HclRawTextToken(raw.content(), content.content(), start, end);
  }
}
