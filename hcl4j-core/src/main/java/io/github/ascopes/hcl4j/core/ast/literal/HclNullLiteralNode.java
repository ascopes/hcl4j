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
package io.github.ascopes.hcl4j.core.ast.literal;

import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A null literal.
 *
 * @param token the original token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclNullLiteralNode(
    @Override HclToken token
) implements HclLiteralValueNode<@Nullable Void> {

  /**
   * Get the null value.
   *
   * @return {@code null}, always.
   */
  @Override
  @Nullable
  public Void value() {
    return null;
  }
}
