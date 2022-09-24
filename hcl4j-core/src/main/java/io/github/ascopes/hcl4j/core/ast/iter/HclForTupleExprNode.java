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
 * A for-expression across a tuple.
 *
 * @param leftToken  the left square bracket token.
 * @param intro      the header of the for-expression.
 * @param expression the expression to evaluate and yield for each iteration.
 * @param condition  the optional condition to apply on each iteration.
 * @param rightToken the right square bracket token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclForTupleExprNode(
    HclToken leftToken,
    HclForIntroNode intro,
    HclExpressionNode expression,
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
