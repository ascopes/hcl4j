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
import io.github.ascopes.hcl4j.core.lexer.HclDefaultLexer;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;

/**
 * Strategy for tokenizing content that occurs within interpolation blocks or directive blocks.
 *
 * <p>This delegates to {@link HclConfigLexerStrategy} internally, but provides additional
 * behaviours that would not be handled elsewhere, such as the "<code>~}</code>" token.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclTemplateExpressionLexerStrategy extends HclCommonLexerStrategyBase {

  private final HclConfigLexerStrategy configLexerStrategy;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public HclTemplateExpressionLexerStrategy(HclDefaultLexer context) {
    super(context);
    configLexerStrategy = new HclConfigLexerStrategy(context);
  }

  @Override
  public HclToken nextToken() throws HclProcessingException {
    return switch (context.charSource().peek(0)) {
      case EOF -> {
        context.popStrategy();
        yield consumeEndOfFile();
      }
      case '~' -> consumeTilde();
      case '}' -> consumeRightBrace();
      default -> configLexerStrategy.nextToken();
    };
  }

  private HclToken consumeTilde() throws HclProcessingException {
    return newToken(HclTokenType.TRIM, 1);
  }

  private HclToken consumeRightBrace() throws HclProcessingException {
    var token = newToken(HclTokenType.RIGHT_BRACE, 1);
    context.popStrategy();
    return token;
  }
}
