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
import java.util.List;

/**
 * Valid types of collection value literals.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.EXPERIMENTAL)
public sealed interface CollectionValue extends ExprTerm {

  /**
   * A tuple value literal.
   *
   * @param range    the range of the node.
   * @param elements the tuple elements
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TupleValue(
      Range range,
      List<? extends Expression> elements
  ) implements CollectionValue {}

  /**
   * An object value literal.
   *
   * @param range    the range of the node.
   * @param elements the object elements
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record ObjectValue(
      Range range,
      List<ObjectElem> elements
  ) implements CollectionValue {}

  /**
   * An object attribute literal.
   *
   * @param range                  the range of the node.
   * @param key                    the key of the attribute.
   * @param value                  the value of the attribute.
   * @param isKeyVariableReference {@code true} if the key should be de-referenced first,
   *                               {@code false} otherwise
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record ObjectElem(
      Range range,
      Identifier key,
      Expression value,
      boolean isKeyVariableReference
  ) implements Node {}
}
