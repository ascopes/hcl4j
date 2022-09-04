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

/**
 * Valid expression terms that hold templates or string values.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface TemplateExpr extends ExprTerm {

  /**
   * A quoted template.
   *
   * @param range the range of the node.
   * @param template the template contents.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record QuotedTemplate(Range range, Template template) implements TemplateExpr {}

  /**
   * A heredoc template.
   *
   * @param range the range of the node.
   * @param template the template contents.
   * @param indent {@code true} if the heredoc uses the indent marker, {@code false} otherwise.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HeredocTemplate(Range range, Template template, boolean indent) implements TemplateExpr {}

  /**
   * A string literal. This is mostly the same as a {@link QuotedTemplate}, but no template
   * expressions are permitted within the template itself.
   *
   * @param range the range of the node.
   * @param value the string contents.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record StringLit(
      Range range,
      CharSequence value
  ) implements TemplateExpr, BodyItem.BlockIdentifier {}
}
