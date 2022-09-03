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
import io.github.ascopes.hcl4j.core.inputs.Range;

/**
 * Valid types of operation.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.EXPERIMENTAL)
public sealed interface Operation extends ExprTerm, Expression {

  /**
   * A unary operation.
   *
   * @param range the range of the node.
   * @param operator the operator being applied to the term.
   * @param value the value of the operation.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record UnaryOp(Range range, UnaryOperator operator, ExprTerm value) implements Operation {}

  /**
   * Valid types of unary operator.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  enum UnaryOperator {
    MINUS,
    NOT,
  }

  /**
   * A binary operation.
   *
   * @param range the range of the node.
   * @param leftValue the left-hand side of the binary operation.
   * @param operator the binary operator.
   * @param rightValue the right-hand side of the binary operation.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record BinaryOp(
      Range range,
      ExprTerm leftValue,
      BinaryOperator operator,
      ExprTerm rightValue
  ) implements Operation {}

  /**
   * Valid binary operators.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  enum BinaryOperator {
    EQUAL,
    NOT_EQUAL,
    LESS,
    GREATER,
    LESS_EQUAL,
    GREATER_EQUAL,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    AND,
    OR,
    // Pretty sure this is erroneous in the spec, see https://github.com/hashicorp/hcl/pull/550
    NOT,
  }
}
