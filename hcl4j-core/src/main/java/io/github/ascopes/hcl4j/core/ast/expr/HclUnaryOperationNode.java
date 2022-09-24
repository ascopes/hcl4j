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
package io.github.ascopes.hcl4j.core.ast.expr;

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A unary operation.
 *
 * @param operatorToken the operator being applied to the term.
 * @param value         the value of the operation.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclUnaryOperationNode(
    HclToken operatorToken,
    HclExpressionNode value
) implements HclExpressionNode {

  @Override
  public HclLocation start() {
    return operatorToken.start();
  }

  @Override
  public HclLocation end() {
    return value.end();
  }
}
