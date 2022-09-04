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

import io.github.ascopes.hcl4j.core.annotations.Nullable;
import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.util.List;

/**
 * Valid types of for-expression.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface ForExpr extends ExprTerm {

  /**
   * For expression header.
   *
   * @param forToken              the {@code for} keyword token.
   * @param identifier            the initial identifier.
   * @param additionalIdentifiers additional identifiers to unwrap.
   * @param inToken               the {@code in} keyword token.
   * @param inExpression          the expression to iterate across and unwrap.
   * @param colonToken            the {@code :} keyword token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForIntro(
      Token forToken,
      Identifier identifier,
      List<AdditionalForIdentifier> additionalIdentifiers,
      Token inToken,
      Expression inExpression,
      Token colonToken
  ) implements Node {

    @Override
    public Location start() {
      return forToken.start();
    }

    @Override
    public Location end() {
      return colonToken.end();
    }
  }

  /**
   * A conditional filter clause to apply to each item of an iterable expression.
   *
   * @param ifToken      the {@code if} keyword.
   * @param ifExpression the expression to evaluate.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForCond(
      Token ifToken,
      Expression ifExpression
  ) implements Node {

    @Override
    public Location start() {
      return ifToken.start();
    }

    @Override
    public Location end() {
      return ifExpression.end();
    }
  }

  /**
   * A for-expression across a tuple.
   *
   * @param leftToken  the left square bracket token.
   * @param intro      the header of the for-expression.
   * @param expression the expression to evaluate and yield for each iteration.
   * @param condition  the optional condition to apply on each iteration.
   * @param rightToken the right square bracket token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForTupleExpr(
      Token leftToken,
      ForIntro intro,
      Expression expression,
      @Nullable ForCond condition,
      Token rightToken
  ) implements ForExpr {

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return rightToken.end();
    }
  }

  /**
   * A for-expression across an object.
   *
   * @param leftToken       the left opening brace token.
   * @param intro           the header of the for-expression.
   * @param keyExpression   the expression that yields each key.
   * @param fatArrowToken   the fat arrow operator token.
   * @param valueExpression the expression that yields each value.
   * @param ellipsisToken   the ellipsis token, if present. otherwise.
   * @param condition       the optional condition to apply on each iteration.
   * @param rightToken      the right closing brace token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForObjectExpr(
      Token leftToken,
      ForIntro intro,
      Expression keyExpression,
      Token fatArrowToken,
      Expression valueExpression,
      @Nullable Token ellipsisToken,
      @Nullable ForCond condition,
      Token rightToken
  ) implements ForExpr {

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return rightToken.end();
    }
  }
}
