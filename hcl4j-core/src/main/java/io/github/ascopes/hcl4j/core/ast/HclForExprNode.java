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

import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode.HclIdentifierNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * Valid types of for-expression.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclForExprNode extends HclExprTermNode {

  /**
   * For expression header.
   *
   * @param forToken         the {@code for} keyword token.
   * @param firstIdentifier  the first identifier (mandatory).
   * @param commaToken       the optional commaToken before the optional second identifier.
   * @param secondIdentifier the optional second identifier.
   * @param inToken          the {@code in} keyword token.
   * @param inExpression     the expression to iterate across and unwrap.
   * @param colonToken       the {@code :} keyword token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclForIntroNode(
      HclToken forToken,
      HclIdentifierNode firstIdentifier,
      @Nullable HclToken commaToken,
      HclIdentifierNode secondIdentifier,
      HclToken inToken,
      HclExpressionNode inExpression,
      HclToken colonToken
  ) implements HclNode {

    @Override
    public HclLocation start() {
      return forToken.start();
    }

    @Override
    public HclLocation end() {
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
  record HclForConditionNode(
      HclToken ifToken,
      HclExpressionNode ifExpression
  ) implements HclNode {

    @Override
    public HclLocation start() {
      return ifToken.start();
    }

    @Override
    public HclLocation end() {
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
  record HclForTupleExprNode(
      HclToken leftToken,
      HclForIntroNode intro,
      HclExpressionNode expression,
      @Nullable HclForConditionNode condition,
      HclToken rightToken
  ) implements HclForExprNode {

    @Override
    public HclLocation start() {
      return leftToken.start();
    }

    @Override
    public HclLocation end() {
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
  record HclForObjectExprNode(
      HclToken leftToken,
      HclForIntroNode intro,
      HclExpressionNode keyExpression,
      HclToken fatArrowToken,
      HclExpressionNode valueExpression,
      @Nullable HclToken ellipsisToken,
      @Nullable HclForConditionNode condition,
      HclToken rightToken
  ) implements HclForExprNode {

    @Override
    public HclLocation start() {
      return leftToken.start();
    }

    @Override
    public HclLocation end() {
      return rightToken.end();
    }
  }
}
