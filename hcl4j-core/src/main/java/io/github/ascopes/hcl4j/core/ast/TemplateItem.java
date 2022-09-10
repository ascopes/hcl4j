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
 * Valid types of element within a template.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface TemplateItem extends Node {

  /**
   * A literal piece of valueToken within a template.
   *
   * @param valueToken the raw content token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateLiteral(Token valueToken) implements TemplateItem {

    @Override
    public Location start() {
      return valueToken.start();
    }

    @Override
    public Location end() {
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
  record TemplateInterpolation(
      Token leftToken,
      Expression expression,
      Token rightToken
  ) implements TemplateItem {

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
   * An {@code if} directive.
   *
   * @param ifPart    the {@code if} clause.
   * @param elsePart  the {@code else} clause, if present.
   * @param endIfPart the {@code endif} clause.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateIf(
      TemplateIfPart ifPart,
      @Nullable TemplateElsePart elsePart,
      TemplateEndIfPart endIfPart
  ) implements TemplateItem {

    @Override
    public Location start() {
      return ifPart.start();
    }

    @Override
    public Location end() {
      return endIfPart.end();
    }
  }

  /**
   * The {@code if} condition in a {@link TemplateIf}.
   *
   * @param leftToken  the opening delimiter token.
   * @param leftTrimToken the trim marker for the left token, or {@code null} if not provided.
   * @param ifToken    the "if" keyword token.
   * @param expression the opening condition expression.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken the closing delimiter token.
   * @param template   the template to evaluate if the condition evaluates to true.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateIfPart(
      Token leftToken,
      @Nullable Token leftTrimToken,
      Token ifToken,
      Expression expression,
      @Nullable Token rightTrimToken,
      Token rightToken,
      Template template
  ) implements Node {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return template.end();
    }
  }

  /**
   * The {@code else} condition in a {@link TemplateIf}.
   *
   * @param leftToken  the opening delimiter token.
   * @param leftTrimToken the trim marker for the left token, or {@code null} if not provided.
   * @param elseToken  the "else" keyword token.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken the closing delimiter token.
   * @param template   the template to evaluate if the condition evaluates to true.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateElsePart(
      Token leftToken,
      @Nullable Token leftTrimToken,
      Token elseToken,
      @Nullable Token rightTrimToken,
      Token rightToken,
      Template template
  ) implements Node {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return template.end();
    }
  }

  /**
   * The {@code endif} part in a {@link TemplateIf}.
   *
   * @param leftToken  the opening delimiter token.
   * @param leftTrimToken the trim marker for the left token, or {@code null} if not provided.
   * @param endIfToken the "endif" keyword token.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken the closing delimiter token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateEndIfPart(
      Token leftToken,
      @Nullable Token leftTrimToken,
      Token endIfToken,
      @Nullable Token rightTrimToken,
      Token rightToken
  ) implements Node {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

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
   * A {@code for} directive.
   *
   * @param forPart    the {@code for} clause.
   * @param template   the template to evaluate for each iteration.
   * @param endForPart the {@code endfor} clause.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateFor(
      TemplateForPart forPart,
      Template template,
      TemplateEndForPart endForPart
  ) implements TemplateItem {

    @Override
    public Location start() {
      return forPart.start();
    }

    @Override
    public Location end() {
      return endForPart.end();
    }
  }

  /**
   * The {@code for} condition in a {@link TemplateFor}.
   *
   * @param leftToken                the opening delimiter.
   * @param leftTrimToken the trim marker for the left token, or {@code null} if not provided.
   * @param forToken                 the {@code for} keyword.
   * @param identifier               the first identifier.
   * @param additionalForIdentifiers a list of additional identifiers to unwrap.
   * @param inToken                  the {@code in} keyword.
   * @param expression               the expression that evaluates to a tuple.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken               the closing delimiter.
   * @param template                 the template to evaluate for each iteration.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateForPart(
      Token leftToken,
      @Nullable Token leftTrimToken,
      Token forToken,
      Identifier identifier,
      List<AdditionalForIdentifier> additionalForIdentifiers,
      Token inToken,
      Expression expression,
      @Nullable Token rightTrimToken,
      Token rightToken,
      Template template
  ) implements Node {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return template.end();
    }
  }

  /**
   * The {@code endfor} clause in a {@link TemplateFor}.
   *
   * @param leftToken   the opening delimiter.
   * @param leftTrimToken the trim marker for the left token, or {@code null} if not provided.
   * @param endForToken the {@code endfor} keyword.
   * @param rightTrimToken the trim marker for the right token, or {@code null} if not provided.
   * @param rightToken  the closing delimiter.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record TemplateEndForPart(
      Token leftToken,
      @Nullable Token leftTrimToken,
      Token endForToken,
      @Nullable Token rightTrimToken,
      Token rightToken
  ) implements Node {

    public boolean leftTrimmed() {
      return leftTrimToken != null;
    }

    public boolean rightTrimmed() {
      return rightTrimToken != null;
    }

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return rightToken.end();
    }
  }
}
