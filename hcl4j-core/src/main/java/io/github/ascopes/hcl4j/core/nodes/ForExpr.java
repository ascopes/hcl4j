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

import io.github.ascopes.hcl4j.core.inputs.Range;
import java.util.List;
import java.util.Optional;

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
   * @param range the range of the node.
   * @param identifiers the identifiers to unwrap.
   * @param inExpression the expression to iterate across and unwrap.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForIntro(
      Range range,
      List<? extends Identifier> identifiers,
      Expression inExpression
  ) implements Node {}

  /**
   * A conditional filter clause to apply to each item of an iterable expression.
   *
   * @param range the range of the node.
   * @param ifExpression the expression to evaluate.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForCond(
      Range range,
      Expression ifExpression
  ) implements Node {}

  /**
   * A for-expression across a tuple.
   *
   * @param range the range of the node.
   * @param intro the header of the for-expression.
   * @param expression the expression to evaluate and yield for each iteration.
   * @param condition the optional condition to apply on each iteration.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForTupleExpr(
      Range range,
      ForIntro intro,
      Expression expression,
      Optional<ForCond> condition
  ) implements ForExpr {}

  /**
   * A for-expression across an object.
   *
   * @param range the range of the node.
   * @param intro the header of the for-expression.
   * @param keyExpression the expression that yields each key.
   * @param valueExpression the expression that yields each value.
   * @param ellipsis {@code true} if ellipsis were applied after the value, {@code false} otherwise.
   * @param condition the optional condition to apply on each iteration.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record ForObjectExpr(
      Range range,
      ForIntro intro,
      Expression keyExpression,
      Expression valueExpression,
      boolean ellipsis,
      Optional<ForCond> condition
  ) implements ForExpr {}
}
