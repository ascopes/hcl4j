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

package io.github.ascopes.hcl4j.core.ast;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;

/**
 * A conditional ternary operator.
 *
 * @param condition     the conditional expression.
 * @param questionToken the question-mark token.
 * @param ifTrue        the first clause of the condition.
 * @param colonToken    the colon token.
 * @param ifFalse       the second clause of the condition.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record Conditional(
    Expression condition,
    Token questionToken,
    Expression ifTrue,
    Token colonToken,
    Expression ifFalse
) implements Expression {

  @Override
  public Location start() {
    return condition.start();
  }

  @Override
  public Location end() {
    return ifFalse.end();
  }
}
