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

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenErrorMessage;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;

/**
 * Strategy for tokenizing content that occurs within interpolation blocks or directive blocks.
 *
 * <p>This delegates to {@link ConfigLexerStrategy} internally, but provides additional behaviours
 * that would not be handled elsewhere, such as the "<code>~}</code>" token.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class TemplateExpressionLexerStrategy extends CommonLexerStrategy {

  private final ConfigLexerStrategy configLexerStrategy;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public TemplateExpressionLexerStrategy(LexerContext context) {
    super(context);
    configLexerStrategy = new ConfigLexerStrategy(context);
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    if (context.charSource().peek(0) == '~') {
      return consumeTilde();
    } else {
      return configLexerStrategy.nextToken();
    }
  }

  @CheckReturnValue
  private Token consumeTilde() throws IOException {
    if (context.charSource().peek(1) == '}') {
      context.popStrategy();
      return newToken(TokenType.RIGHT_BRACE_TRIM, 2);
    }

    return newError(TokenErrorMessage.UNKNOWN_OPERATOR, 1);
  }
}
