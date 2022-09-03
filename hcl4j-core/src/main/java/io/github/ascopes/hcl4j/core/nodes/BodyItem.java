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
 * Valid body items.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.EXPERIMENTAL)
public sealed interface BodyItem extends Node {

  /**
   * A block body item.
   *
   * @param range           the range of the node.
   * @param firstIdentifier the first identifier.
   * @param identifiers     any additional identifiers.
   * @param body            the block body.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record Block(
      Range range,
      Identifier firstIdentifier,
      List<? extends BlockIdentifier> identifiers,
      Body body
  ) implements BodyItem {}

  /**
   * A single attribute with an assigned value.
   *
   * @param range      the range of the node.
   * @param identifier the attribute identifier.
   * @param expression the attribute expression.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record Attribute(Range range, Identifier identifier, Expression expression)
      implements BodyItem {}

  /**
   * Sealed interface for valid additional identifiers in a block. This does not apply to the very
   * first identifier, which must always be a {@link Identifier}.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  sealed interface BlockIdentifier extends Node permits Identifier, TemplateExpr.StringLit {

    CharSequence value();
  }
}
