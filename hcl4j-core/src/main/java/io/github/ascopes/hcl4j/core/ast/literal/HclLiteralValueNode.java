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

import io.github.ascopes.hcl4j.core.ast.expr.HclExprTermNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * Valid types of literal, as defined in the HCL spec.
 *
 * @param <T> the literal value type.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface HclLiteralValueNode<T> extends HclExprTermNode {

  /**
   * Get the original token.
   *
   * @return the original token.
   */
  HclToken token();

  /**
   * Get the literal value.
   *
   * @return the literal value.
   */
  T value();

  @Override
  default HclLocation start() {
    return token().start();
  }

  @Override
  default HclLocation end() {
    return token().end();
  }

}
