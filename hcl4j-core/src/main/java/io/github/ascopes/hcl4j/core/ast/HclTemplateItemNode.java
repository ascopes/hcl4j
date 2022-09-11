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
 * Valid types of element within a template.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclTemplateItemNode extends HclNode {

  /**
   * A literal piece of valueToken within a template.
   *
   * @param valueToken the raw content token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateLiteralNode(HclToken valueToken) implements HclTemplateItemNode {

    @Override
    public HclLocation start() {
      return valueToken.start();
    }

    @Override
    public HclLocation end() {
      return valueToken.end();
    }

    public CharSequence value() {
      return valueToken.content();
    }
  }

  /**
   * An interpolation template expression.
   *
   * @param leftToken  the opening delimiter token.
   * @param expression the expression to evaluate and interpolate.
   * @param rightToken the closing delimiter token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateInterpNode(
      HclToken leftToken,
      HclExpressionNode expression,
      HclToken rightToken
  ) implements HclTemplateItemNode {

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
   * An {@code if} directive.
   *
   * @param ifPart    the {@code if} clause.
   * @param elsePart  the {@code else} clause, if present.
   * @param endIfPart the {@code endif} clause.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateIfNode(
      HclTemplateIfPartNode ifPart,
      @Nullable HclTemplateItemNode.HclTemplateElsePartNode elsePart,
      HclTemplateEndPartNode endIfPart
  ) implements HclTemplateItemNode {

    @Override
    public HclLocation start() {
      return ifPart.start();
    }

    @Override
    public HclLocation end() {
      return endIfPart.end();
    }
  }

  /**
   * The {@code if} condition in a {@link HclTemplateIfNode}.
   *
   * @param leftToken      the opening delimiter token.
   * @param leftTrimToken  the trim marker for the left token, or {@code null} if not provided.
   * @param ifToken        the "if" keyword token.
   * @param expression     the opening condition expression.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken     the closing delimiter token.
   * @param template       the template to evaluate if the condition evaluates to true.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateIfPartNode(
      HclToken leftToken,
      @Nullable HclToken leftTrimToken,
      HclToken ifToken,
      HclExpressionNode expression,
      @Nullable HclToken rightTrimToken,
      HclToken rightToken,
      HclTemplateNode template
  ) implements HclNode {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public HclLocation start() {
      return leftToken.start();
    }

    @Override
    public HclLocation end() {
      return template.end();
    }
  }

  /**
   * The {@code else} condition in a {@link HclTemplateIfNode}.
   *
   * @param leftToken      the opening delimiter token.
   * @param leftTrimToken  the trim marker for the left token, or {@code null} if not provided.
   * @param elseToken      the "else" keyword token.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken     the closing delimiter token.
   * @param template       the template to evaluate if the condition evaluates to true.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateElsePartNode(
      HclToken leftToken,
      @Nullable HclToken leftTrimToken,
      HclToken elseToken,
      @Nullable HclToken rightTrimToken,
      HclToken rightToken,
      HclTemplateNode template
  ) implements HclNode {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public HclLocation start() {
      return leftToken.start();
    }

    @Override
    public HclLocation end() {
      return template.end();
    }
  }

  /**
   * The {@code endif} part in a {@link HclTemplateIfNode} or {@link HclTemplateForNode}.
   *
   * @param leftToken      the opening delimiter token.
   * @param leftTrimToken  the trim marker for the left token, or {@code null} if not provided.
   * @param endToken       the "endif" keyword token or "endfor" keyword token.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken     the closing delimiter token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateEndPartNode(
      HclToken leftToken,
      @Nullable HclToken leftTrimToken,
      HclToken endToken,
      @Nullable HclToken rightTrimToken,
      HclToken rightToken
  ) implements HclNode {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

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
   * A {@code for} directive.
   *
   * @param forPart    the {@code for} clause.
   * @param template   the template to evaluate for each iteration.
   * @param endForPart the {@code endfor} clause.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateForNode(
      HclTemplateForPartNode forPart,
      HclTemplateNode template,
      HclTemplateEndPartNode endForPart
  ) implements HclTemplateItemNode {

    @Override
    public HclLocation start() {
      return forPart.start();
    }

    @Override
    public HclLocation end() {
      return endForPart.end();
    }
  }

  /**
   * The {@code for} condition in a {@link HclTemplateForNode}.
   *
   * @param leftToken                the opening delimiter.
   * @param leftTrimToken            the trim marker for the left token, or {@code null} if not
   *                                 provided.
   * @param forToken                 the {@code for} keyword.
   * @param identifier               the first identifier.
   * @param additionalForIdentifiers a list of additional identifiers to unwrap.
   * @param inToken                  the {@code in} keyword.
   * @param expression               the expression that evaluates to a tuple.
   * @param rightTrimToken           the trim marker for the right token, or {@code null} if not
   *                                 provided.
   * @param rightToken               the closing delimiter.
   * @param template                 the template to evaluate for each iteration.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclTemplateForPartNode(
      HclToken leftToken,
      @Nullable HclToken leftTrimToken,
      HclToken forToken,
      HclIdentifierLikeNode identifier,
      List<HclAdditionalForIdentifier> additionalForIdentifiers,
      HclToken inToken,
      HclExpressionNode expression,
      @Nullable HclToken rightTrimToken,
      HclToken rightToken,
      HclTemplateNode template
  ) implements HclNode {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public HclLocation start() {
      return leftToken.start();
    }

    @Override
    public HclLocation end() {
      return template.end();
    }
  }
}
