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
import io.github.ascopes.hcl4j.core.annotations.Nullable;
import io.github.ascopes.hcl4j.core.inputs.RawContentBuffer;
import io.github.ascopes.hcl4j.core.lexer.Lexer;
import io.github.ascopes.hcl4j.core.tokens.RawTextToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;

/**
 * Lexer strategy for tokenizing content within a heredoc directive until the heredoc is
 * terminated.
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
 *   <li>An end-of-file marker will pop the current mode.</li>
 *   <li>If the identifier matches the provided identifier given during construction,
 *      followed by a new line character, then we consider that to be the end of the heredoc.
 *      This lexer strategy will be popped and the identifier up to but not including the new line
 *      will be emitted as an {@link TokenType#IDENTIFIER}.</li>
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
 *      The valueToken will then be emitted as {@link TokenType#RAW_TEXT} as long as at least one
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
public final class HeredocLexerStrategy extends CommonLexerStrategy {

  private final CharSequence identifier;

  /**
   * Initialize the strategy.
   *
   * @param context    the context to use.
   * @param identifier the closing identifier to use.
   */
  public HeredocLexerStrategy(Lexer context, CharSequence identifier) {
    super(context);
    this.identifier = identifier;
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    if (isClosingIdentifierAhead()) {
      // Time to pop the lexer mode, we have reached the end of the heredoc.
      context.popStrategy();
      return consumeIdentifier();
    }

    if (context.charSource().peek(0) == EOF) {
      // EOF. Shouldn't have happened. Parser should deal with this.
      context.popStrategy();
      return consumeEndOfFile();
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

    return consumeSomeText();
  }

  @CheckReturnValue
  @Nullable
  private boolean isClosingIdentifierAhead() throws IOException {
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

  @CheckReturnValue
  private Token consumeSomeText() throws IOException {
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

    return new RawTextToken(raw.content(), content.content(), start, end);
  }
}
