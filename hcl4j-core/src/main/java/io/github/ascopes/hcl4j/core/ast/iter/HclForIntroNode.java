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
import io.github.ascopes.hcl4j.core.ast.id.HclIdentifierNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocatable;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * For expression header.
 *
 * @param forToken         the {@code for} keyword token.
 * @param firstIdentifier  the first identifier (mandatory).
 * @param commaToken       the optional commaToken before the optional second identifier.
 * @param secondIdentifier the optional second identifier.
 * @param inToken          the {@code in} keyword token.
 * @param inExpression     the expression to iterate across and unwrap.
 * @param colonToken       the {@code :} keyword token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclForIntroNode(
    HclToken forToken,
    HclIdentifierNode firstIdentifier,
    @Nullable HclToken commaToken,
    HclIdentifierNode secondIdentifier,
    HclToken inToken,
    HclExpressionNode inExpression,
    HclToken colonToken
) implements HclLocatable {

  @Override
  public HclLocation start() {
    return forToken.start();
  }

  @Override
  public HclLocation end() {
    return colonToken.end();
  }
}
