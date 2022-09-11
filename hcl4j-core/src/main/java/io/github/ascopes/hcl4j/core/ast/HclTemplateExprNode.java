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

import io.github.ascopes.hcl4j.core.ast.HclBodyItemNode.HclBlockIdentifierNode;
import io.github.ascopes.hcl4j.core.ast.HclTemplateItemNode.HclTemplateLiteralNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * Valid expression terms that hold templates or string values.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclTemplateExprNode extends HclExprTermNode {

  /**
   * A quoted template.
   *
   * @param leftQuoteToken  the leftToken quotation.
   * @param template        the template contents.
   * @param rightQuoteToken the rightToken quotation.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclQuotedTemplateNode(
      HclToken leftQuoteToken,
      HclTemplateNode template,
      HclTemplateNode rightQuoteToken
  ) implements HclTemplateExprNode {

    @Override
    public HclLocation start() {
      return leftQuoteToken.start();
    }

    @Override
    public HclLocation end() {
      return rightQuoteToken.end();
    }
  }

  /**
   * A heredoc template.
   *
   * @param anchorToken            the anchor token.
   * @param indentToken            the indent token marker, if present (or else {@code null}.
   * @param openingIdentifierToken the opening identifier token.
   * @param template               the template contents.
   * @param closingIdentifierToken the closing identifier token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclHeredocTemplateNode(
      HclToken anchorToken,
      @Nullable HclToken indentToken,
      HclToken openingIdentifierToken,
      HclTemplateNode template,
      HclToken closingIdentifierToken
  ) implements HclTemplateExprNode {

    @Override
    public HclLocation start() {
      return anchorToken.start();
    }

    @Override
    public HclLocation end() {
      return closingIdentifierToken.end();
    }
  }

  /**
   * A string literal. This is mostly the same as a {@link HclQuotedTemplateNode}, but no template
   * expressions are permitted within the template itself.
   *
   * @param leftQuoteToken  the leftToken quotation.
   * @param templateLiteral the template literal contents.
   * @param rightQuoteToken the rightToken quotation.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclStringLiteralNode(
      HclToken leftQuoteToken,
      HclTemplateLiteralNode templateLiteral,
      HclTemplateNode rightQuoteToken
  ) implements HclTemplateExprNode, HclBlockIdentifierNode {

    @Override
    public HclLocation start() {
      return leftQuoteToken.start();
    }

    @Override
    public HclLocation end() {
      return rightQuoteToken.end();
    }

    @Override
    public CharSequence value() {
      return templateLiteral().value();
    }
  }
}
