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
package io.github.ascopes.hcl4j.core.ast;

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Valid types of literal, as defined in the HCL spec.
 *
 * @param <T> the literal value type.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclLiteralValueNode<T> extends HclExprTermNode {

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

  /**
   * Valid types of numeric literal.
   *
   * @param <N> the number type.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface HclNumericLiteralNode<N extends Number> extends HclLiteralValueNode<N> {}

  /**
   * A boolean literal.
   *
   * @param token the original token.
   * @param value the value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclBooleanLiteralNode(
      @Override HclToken token,
      @Override Boolean value
  ) implements HclLiteralValueNode<Boolean> {}

  /**
   * A null literal.
   *
   * @param token the original token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclNullLiteralNode(
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

  /**
   * An integer value literal.
   *
   * @param token the original token.
   * @param value the integer value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclIntegerLiteralNode(
      @Override HclToken token,
      @Override BigInteger value
  ) implements HclNumericLiteralNode<BigInteger> {}

  /**
   * A real value literal.
   *
   * @param token the original token.
   * @param value the real value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclRealLiteralNode(
      @Override HclToken token,
      @Override BigDecimal value
  ) implements HclNumericLiteralNode<BigDecimal> {}
}
