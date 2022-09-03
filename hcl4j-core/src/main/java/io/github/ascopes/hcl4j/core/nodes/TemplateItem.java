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
import java.util.Optional;

/**
 * Valid types of element within a template.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.EXPERIMENTAL)
public sealed interface TemplateItem {

  /**
   * A literal piece of text within a template.
   *
   * @param range the range of the node.
   * @param text the string value.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateLiteral(Range range, CharSequence text) implements TemplateItem {}

  /**
   * An interpolation template expression.
   *
   * @param range the range of the node.
   * @param stripLeft {@code true} if the left should be stripped.
   * @param expression the expression to evaluate and interpolate.
   * @param stripRight {@code true} if the right should be stripped.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateInterpolation(
      Range range,
      boolean stripLeft,
      Expression expression,
      boolean stripRight
  ) implements TemplateItem {}

  /**
   * An {@code if} directive.
   *
   * @param range the range of the node.
   * @param ifPart the {@code if} clause.
   * @param elsePart the {@code else} clause, if present.
   * @param endIfPart the {@code endif} clause.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateIf(
      Range range,
      TemplateIfPart ifPart,
      Optional<TemplateElsePart> elsePart,
      TemplateEndIfPart endIfPart
  ) {}

  /**
   * The {@code if} condition in a {@link TemplateIf}.
   *
   * @param range the range of the node.
   * @param stripLeft {@code true} if the left should be stripped.
   * @param condition the condition to evaluate.
   * @param stripRight {@code true} if the right should be stripped.
   * @param template the template to evaluate if the condition evaluates to true.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateIfPart(
      Range range,
      boolean stripLeft,
      Expression condition,
      boolean stripRight,
      Template template
  ) implements Node {}

  /**
   * The {@code else} condition in a {@link TemplateIf}.
   *
   * @param range the range of the node.
   * @param stripLeft {@code true} if the left should be stripped.
   * @param stripRight {@code true} if the right should be stripped.
   * @param template the template to evaluate if the condition evaluates to false.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateElsePart(
      Range range,
      boolean stripLeft,
      boolean stripRight,
      Template template
  ) implements Node {}

  /**
   * The {@code endif} part in a {@link TemplateIf}.
   *
   * @param range the range of the node.
   * @param stripLeft {@code true} if the left should be stripped.
   * @param stripRight {@code true} if the right should be stripped.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateEndIfPart(
      Range range,
      boolean stripLeft,
      boolean stripRight
  ) implements Node {}

  /**
   * A {@code for} directive.
   *
   * @param range the range of the node.
   * @param forPart the {@code for} clause.
   * @param template the template to evaluate for each iteration.
   * @param endForPart the {@code endfor} clause.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateFor(
      Range range,
      TemplateForPart forPart,
      Template template,
      TemplateEndForPart endForPart
  ) implements TemplateItem {}

  /**
   * The {@code for} condition in a {@link TemplateFor}.
   *
   * @param range the range of the node.
   * @param stripLeft {@code true} if the left should be stripped.
   * @param identifiers the identifiers to unwrap.
   * @param expression the expression to iterate over and unwrap.
   * @param stripRight {@code true} if the right should be stripped.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateForPart(
      Range range,
      boolean stripLeft,
      List<? extends Identifier> identifiers,
      Expression expression,
      boolean stripRight
  ) implements Node {}

  /**
   * The {@code endfor} clause in a {@link TemplateFor}.
   *
   * @param range the range of the node.
   * @param stripLeft {@code true} if the left should be stripped.
   * @param stripRight {@code true} if the right should be stripped.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  @Api(Visibility.EXPERIMENTAL)
  record TemplateEndForPart(
      Range range,
      boolean stripLeft,
      boolean stripRight
  ) implements Node {}
}
