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
import io.github.ascopes.hcl4j.core.inputs.HclLocatable;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * The {@code if} condition in a {@link HclTemplateIfNode}.
 *
 * @param leftToken      the opening delimiter token.
 * @param leftTrimToken  the trim marker for the left token, or {@code null} if not provided.
 * @param ifToken        the "if" keyword token.
 * @param expression     the opening condition expression.
 * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
 * @param rightToken     the closing delimiter token.
 * @param template       the template to evaluate if the condition evaluates to true.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTemplateIfPartNode(
    HclToken leftToken,
    @Nullable HclToken leftTrimToken,
    HclToken ifToken,
    HclExpressionNode expression,
    @Nullable HclToken rightTrimToken,
    HclToken rightToken,
    HclTemplateContentNode template
) implements HclLocatable, HclTemplatePartNode {

  public boolean leftTrimmed() {
    return leftTrimToken != null;
  }

  public boolean rightTrimmed() {
    return rightTrimToken != null;
  }

  @Override
  public HclLocation start() {
    return leftToken.start();
  }

  @Override
  public HclLocation end() {
    return template.end();
  }
}
