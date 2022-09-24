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
package io.github.ascopes.hcl4j.core.ast.template;

import io.github.ascopes.hcl4j.core.ast.expr.HclExpressionNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * An interpolation template expression.
 *
 * @param leftToken      the opening delimiter token.
 * @param leftTrimToken  the optional left trim token, if present.
 * @param expression     the expression to evaluate and interpolate.
 * @param rightTrimToken the optional right trim token, if present.
 * @param rightToken     the closing delimiter token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTemplateInterpolationNode(
    HclToken leftToken,
    @Nullable HclToken leftTrimToken,
    HclExpressionNode expression,
    @Nullable HclToken rightTrimToken,
    HclToken rightToken
) implements HclTemplateItemNode {

  @Override
  public HclLocation start() {
    return leftToken.start();
  }

  @Override
  public HclLocation end() {
    return rightToken.end();
  }
}
