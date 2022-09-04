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

import io.github.ascopes.hcl4j.core.ast.TemplateExpr.StringLit;
import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.util.List;

/**
 * Valid body items.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface BodyItem extends Node {

  /**
   * A block body item.
   *
   * @param identifier            the first identifier.
   * @param additionalIdentifiers any additional identifiers.
   * @param leftToken             the left brace token.
   * @param body                  the block body.
   * @param rightToken            the right brace token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record Block(
      Identifier identifier,
      List<? extends BlockIdentifier> additionalIdentifiers,
      Token leftToken,
      Body body,
      Token rightToken
  ) implements BodyItem {

    @Override
    public Location start() {
      return identifier.start();
    }

    @Override
    public Location end() {
      return rightToken.end();
    }
  }

  /**
   * A single attribute with an assigned value.
   *
   * @param identifier  the attribute identifier.
   * @param assignToken the assignment token.
   * @param expression  the attribute expression.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record Attribute(
      Identifier identifier,
      Token assignToken,
      Expression expression
  ) implements BodyItem {

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
   * Sealed interface for valid additional identifiers in a block. This does not apply to the very
   * first identifier, which must always be a {@link Identifier}.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface BlockIdentifier extends Node permits Identifier, StringLit {

    CharSequence value();
  }
}
