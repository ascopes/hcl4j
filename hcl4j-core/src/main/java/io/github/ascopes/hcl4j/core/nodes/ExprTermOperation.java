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
 * Valid types of operations performed upon nested expression terms.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.EXPERIMENTAL)
public sealed interface ExprTermOperation extends ExprTerm {

  /**
   * A sealed marker interface that marks valid nodes that can be used within the splat of a
   * {@link Splat}.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  sealed interface SplatTerm {}

  /**
   * An index operator term.
   *
   * @param range the range of the node.
   * @param exprTerm the term being indexed.
   * @param index the index being extracted from the term.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record Index(
      Range range,
      ExprTerm exprTerm,
      Expression index
  ) implements ExprTermOperation, SplatTerm {}

  /**
   * An attribute accession operator.
   *
   * @param range the range of the node.
   * @param exprTerm the term having an attribute accessed.
   * @param attribute the attribute being accessed.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record GetAttr(
      Range range,
      ExprTerm exprTerm,
      Identifier attribute
  ) implements ExprTermOperation, SplatTerm {}

  /**
   * A splat operation.
   *
   * @param range the range of the node.
   * @param exprTerm the term being splatted.
   * @param splatTerms any operations to perform on the value before splatting it.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record Splat(
      Range range,
      ExprTerm exprTerm,
      List<? extends SplatTerm> splatTerms
  ) implements ExprTermOperation {}
}
