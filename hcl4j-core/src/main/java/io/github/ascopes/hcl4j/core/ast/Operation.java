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

package io.github.ascopes.hcl4j.core.ast;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;

/**
 * Valid types of operation.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface Operation extends ExprTerm, Expression {

  /**
   * A unary operation.
   *
   * @param operatorToken the operator being applied to the term.
   * @param value         the value of the operation.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record UnaryOp(Token operatorToken, ExprTerm value) implements Operation {

    @Override
    public Location start() {
      return operatorToken.start();
    }

    @Override
    public Location end() {
      return value.end();
    }
  }

  /**
   * A binary operation.
   *
   * @param leftValue     the left-hand side of the binary operation.
   * @param operatorToken the binary operator token.
   * @param rightValue    the right-hand side of the binary operation.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record BinaryOp(
      ExprTerm leftValue,
      Token operatorToken,
      ExprTerm rightValue
  ) implements Operation {

    @Override
    public Location start() {
      return leftValue.start();
    }

    @Override
    public Location end() {
      return rightValue.end();
    }
  }

}
