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
package io.github.ascopes.hcl4j.core.ast.iter;

import io.github.ascopes.hcl4j.core.ast.expr.HclExpressionNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A for-expression across an object.
 *
 * @param leftToken       the left opening brace token.
 * @param intro           the header of the for-expression.
 * @param keyExpression   the expression that yields each key.
 * @param fatArrowToken   the fat arrow operator token.
 * @param valueExpression the expression that yields each value.
 * @param ellipsisToken   the ellipsis token, if present. otherwise.
 * @param condition       the optional condition to apply on each iteration.
 * @param rightToken      the right closing brace token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclForObjectExprNode(
    HclToken leftToken,
    HclForIntroNode intro,
    HclExpressionNode keyExpression,
    HclToken fatArrowToken,
    HclExpressionNode valueExpression,
    @Nullable HclToken ellipsisToken,
    @Nullable HclForConditionNode condition,
    HclToken rightToken
) implements HclForExprNode {

  @Override
  public HclLocation start() {
    return leftToken.start();
  }

  @Override
  public HclLocation end() {
    return rightToken.end();
  }
}
