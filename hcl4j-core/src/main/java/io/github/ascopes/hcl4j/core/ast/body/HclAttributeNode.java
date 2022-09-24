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
package io.github.ascopes.hcl4j.core.ast.body;

import io.github.ascopes.hcl4j.core.ast.expr.HclExpressionNode;
import io.github.ascopes.hcl4j.core.ast.id.HclIdentifierLikeNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A single attribute with an assigned value.
 *
 * @param identifier  the attribute identifier.
 * @param assignToken the assignment token.
 * @param expression  the attribute expression.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclAttributeNode(
    HclIdentifierLikeNode identifier,
    HclToken assignToken,
    HclExpressionNode expression
) implements HclBodyItemNode {

  @Override
  public HclLocation start() {
    return identifier.start();
  }

  @Override
  public HclLocation end() {
    return identifier.end();
  }
}
