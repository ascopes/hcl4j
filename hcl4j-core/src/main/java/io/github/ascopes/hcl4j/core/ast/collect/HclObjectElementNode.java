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
package io.github.ascopes.hcl4j.core.ast.collect;

import io.github.ascopes.hcl4j.core.ast.expr.HclExpressionNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocatable;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * An object attribute literal.
 *
 * @param commaToken      the leading comma token, or {@code null} if not present on the first
 *                        item.
 * @param keyExpression   the key of the attribute.
 * @param keyIsExpression if true, the key is an expression rather than an identifier (meaning it
 *                        has to be dereferences first to get the actual identifier value).
 * @param mapperToken     the colon or equals token separating the key from the value.
 * @param valueExpression the value of the attribute.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclObjectElementNode(
    @Nullable HclToken commaToken,
    HclExpressionNode keyExpression,
    boolean keyIsExpression,
    HclToken mapperToken,
    HclExpressionNode valueExpression
) implements HclLocatable {

  @Override
  public HclLocation start() {
    return commaToken == null ? keyExpression.start() : commaToken.start();
  }

  @Override
  public HclLocation end() {
    return valueExpression.end();
  }
}
