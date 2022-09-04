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
 * Valid types of collection value literals.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface CollectionValue extends ExprTerm {

  /**
   * A tuple value literal.
   *
   * @param leftToken  the opening square bracket token.
   * @param elements   the tuple members.
   * @param rightToken the closing square bracket token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record Tuple(
      Token leftToken,
      List<TupleElement> elements,
      Token rightToken
  ) implements CollectionValue {

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
   * A tuple value element literal.
   *
   * @param expression the expression.
   * @param commaToken the comma. Can be {@code null} on the last item.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TupleElement(
      Expression expression,
      @Nullable Token commaToken
  ) implements Node {

    @Override
    public Location start() {
      return expression.start();
    }

    @Override
    public Location end() {
      return commaToken == null
          ? expression.end()
          : commaToken.end();
    }
  }

  /**
   * An object value literal.
   *
   * <p>This is named {@code Dict} to prevent confusion with {@link java.lang.Object}.
   *
   * @param leftToken  the opening brace token.
   * @param elements   the tuple members.
   * @param rightToken the closing brace token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record Dict(
      Token leftToken,
      List<DictElement> elements,
      Token rightToken
  ) implements CollectionValue {

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
   * An object attribute literal.
   *
   * @param key         the key of the attribute.
   * @param mapperToken the colon or equals token separating the key from the value.
   * @param value       the value of the attribute.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record DictElement(
      Expression key,
      Token mapperToken,
      Expression value
  ) implements Node {

    @Override
    public Location start() {
      return key.start();
    }

    @Override
    public Location end() {
      return null;
    }
  }

  /**
   * Base interface for valid types of object key identifier.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface DictKey extends Node {}

  /**
   * A raw identifier used in an object key.
   *
   * @param identifier the identifier.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record DictIdentifierKey(Identifier identifier) implements DictKey {

    @Override
    public Location start() {
      return identifier.start();
    }

    @Override
    public Location end() {
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
  record DictExpressionKey(Expression expression) implements DictKey {

    @Override
    public Location start() {
      return expression.start();
    }

    @Override
    public Location end() {
      return expression.end();
    }
  }
}
