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

import io.github.ascopes.hcl4j.core.ast.HclVisitable;
import io.github.ascopes.hcl4j.core.ast.expr.HclExpressionNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A tuple value element literal.
 *
 * @param expression the expression.
 * @param commaToken the commaToken. Can be {@code null} on the last item.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTupleElementNode(
    @Nullable HclToken commaToken,
    HclExpressionNode expression
) implements HclVisitable {

  @Override
  public HclLocation start() {
    return commaToken == null ? expression.start() : commaToken.start();
  }

  @Override
  public HclLocation end() {
    return expression.end();
  }
}
