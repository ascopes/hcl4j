/*
 * Copyright (C) 2022 Ashley Scopes
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

package io.github.ascopes.hcl4j.core.nodes;

import io.github.ascopes.hcl4j.core.annotations.Api;
import io.github.ascopes.hcl4j.core.annotations.Api.Visibility;
import io.github.ascopes.hcl4j.core.annotations.Nullable;
import io.github.ascopes.hcl4j.core.inputs.Range;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Valid types of literal, as defined in the HCL spec.
 *
 * @param <T> the literal value type.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.EXPERIMENTAL)
public sealed interface LiteralValue<T> extends ExprTerm {

  /**
   * Get the literal value.
   *
   * @return the literal value.
   */
  T value();

  /**
   * A boolean literal.
   *
   * @param range the range of the node.
   * @param value the value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record BooleanLit(Range range, Boolean value) implements LiteralValue<Boolean> {}

  /**
   * A null literal.
   *
   * @param range the range of the node.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record NullLit(Range range) implements LiteralValue<@Nullable Void> {

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
   * Valid types of numeric literal.
   *
   * @param <T> the number type.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  sealed interface NumericLit<N extends Number> extends LiteralValue<N> {}

  /**
   * An integer value literal.
   *
   * @param range the range of the node.
   * @param value the integer value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record IntLit(Range range, BigInteger value) implements NumericLit<BigInteger> {}

  /**
   * A real value literal.
   *
   * @param range the range of the node.
   * @param value the real value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record RealLit(Range range, BigDecimal value) implements NumericLit<BigDecimal> {}
}
