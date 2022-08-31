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
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import io.github.ascopes.hcl4j.core.tokens.impl.SimpleToken;
import java.io.IOException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Lexer strategy for consuming files that are purely template literals.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This is very similar to {@link HeredocLexerStrategy} and {@link QuotedTemplateLexerStrategy},
 * but this will only treat the end-of-file as the terminating sequence. This implementation is
 * designed for use cases such as the Terraform mechanism of loading a template from an external
 * file.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public final class TemplateFileLexerStrategy extends CommonLexerStrategy {

  /**
   * Initialize this strategy.
   *
   * @param context the lexer context to use.
   */
  public TemplateFileLexerStrategy(LexerContext context) {
    super(context);
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    if (context.charSource().peek(0) == EOF) {
      // EOF.
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

  private Token consumeSomeText() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder()
        .append(context.charSource().read());

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
            buff.append("${");
            context.charSource().advance(3);
            continue;
          }

          buff.append(context.charSource().read());
        }
        case '%' -> {
          if (context.charSource().startsWith("%{")) {
            break loop;
          }

          if (context.charSource().startsWith("%%{")) {
            buff.append("%{");
            context.charSource().advance(3);
            continue;
          }

          buff.append(context.charSource().read());
        }

        default -> buff.append(context.charSource().read());
      }
    }

    return new SimpleToken(TokenType.RAW_TEXT, buff.raw(), location);
  }
}
