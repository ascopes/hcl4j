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

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.util.List;

/**
 * Valid types of collection value literals.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclCollectionValueNode extends HclExprTermNode {

  /**
   * Base interface for valid types of object key identifier.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface HclObjectKeyNode extends HclNode {}

  /**
   * A tuple value literal.
   *
   * @param leftToken    the opening square bracket token.
   * @param elements     the tuple members.
   * @param trailerComma the trailing comma if present and at least one element exists, else
   *                     {@code null}.
   * @param rightToken   the closing square bracket token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTupleNode(
      HclToken leftToken,
      List<HclTupleElementNode> elements,
      @Nullable HclToken trailerComma,
      HclToken rightToken
  ) implements HclCollectionValueNode {

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
   * A tuple value element literal.
   *
   * @param expression the expression.
   * @param commaToken the commaToken. Can be {@code null} on the last item.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTupleElementNode(
      @Nullable HclToken commaToken,
      HclExpressionNode expression
  ) implements HclNode {

    @Override
    public HclLocation start() {
      return commaToken == null ? expression.start() : commaToken.start();
    }

    @Override
    public HclLocation end() {
      return expression.end();
    }
  }

  /**
   * An object value literal.
   *
   * @param leftToken    the opening brace token.
   * @param elements     the tuple members.
   * @param trailerComma the optional trailing comma.
   * @param rightToken   the closing brace token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclObjectNode(
      HclToken leftToken,
      List<HclObjectElementNode> elements,
      @Nullable HclToken trailerComma,
      HclToken rightToken
  ) implements HclCollectionValueNode {

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
   * An object attribute literal.
   *
   * @param commaToken      the leading comma token, or {@code null} if not present on the first
   *                        item.
   * @param keyExpression   the key of the attribute.
   * @param keyIsExpression if true, the key is an expression rather than an identifier (meaning it
   *                        has to be dereferences first to get the actual identifier value).
   * @param mapperToken     the colon or equals token separating the key from the value.
   * @param valueExpression the value of the attribute.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclObjectElementNode(
      @Nullable HclToken commaToken,
      HclExpressionNode keyExpression,
      boolean keyIsExpression,
      HclToken mapperToken,
      HclExpressionNode valueExpression
  ) implements HclNode {

    @Override
    public HclLocation start() {
      return commaToken == null ? keyExpression.start() : commaToken.start();
    }

    @Override
    public HclLocation end() {
      return valueExpression.end();
    }
  }

  /**
   * A raw identifier used in an object key.
   *
   * @param identifier the identifier.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclObjectIdentifierKeyNode(HclIdentifierLikeNode identifier) implements HclObjectKeyNode {

    @Override
    public HclLocation start() {
      return identifier.start();
    }

    @Override
    public HclLocation end() {
      return identifier.end();
    }
  }

  /**
   * An expression that should be evaluated to get the key identifier in an object key.
   *
   * @param expression the expression.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclObjectExpressionKeyNode(HclExpressionNode expression) implements HclObjectKeyNode {

    @Override
    public HclLocation start() {
      return expression.start();
    }

    @Override
    public HclLocation end() {
      return expression.end();
    }
  }
}
