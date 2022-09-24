/*
 * Copyright (C) 2022 - 2022 Ashley Scopes
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

/**
 * An identifier-like value.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclIdentifierLikeNode extends HclNode {

  /**
   * The content of the identifier, or an empty-string where appropriate if no content is provided.
   *
   * @return the content.
   */
  default CharSequence value() {
    var token = contentToken();
    return token == null ? "" : token.content();
  }

  /**
   * The content token, or {@code null} if no content is provided.
   *
   * @return the content token, if present.
   */
  @Nullable
  HclToken contentToken();

  /**
   * An HCL identifier.
   *
   * @param contentToken the text token node, always non-null.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclIdentifierNode(
      @Override HclToken contentToken
  ) implements HclIdentifierLikeNode {

    @Override
    public HclLocation start() {
      return contentToken.start();
    }

    @Override
    public HclLocation end() {
      return contentToken.start();
    }
  }

  /**
   * An HCL string literal identifier.
   *
   * @param openingQuoteToken the opening quote token.
   * @param contentToken      the text token node, if present, otherwise {@code null}.
   * @param closingQuoteToken the closing quote token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclStringLiteralNode(
      HclToken openingQuoteToken,
      @Nullable @Override HclToken contentToken,
      HclToken closingQuoteToken
  ) implements HclIdentifierLikeNode {

    @Override
    public HclLocation start() {
      return openingQuoteToken.start();
    }

    @Override
    public HclLocation end() {
      return closingQuoteToken.start();
    }
  }
}
