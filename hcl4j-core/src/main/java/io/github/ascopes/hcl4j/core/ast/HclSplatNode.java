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
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A splat operation.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclSplatNode extends HclExprTermNode {

  /**
   * An attribute splat node.
   *
   * @param exprTerm the expression term to splat.
   * @param dot      the dot.
   * @param star     the star.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclAttrSplatNode(
      HclExprTermNode exprTerm,
      HclToken dot,
      HclToken star
  ) implements HclSplatNode {

    @Override
    public HclLocation start() {
      return exprTerm.start();
    }

    @Override
    public HclLocation end() {
      return star.end();
    }
  }

  /**
   * An full splat node.
   *
   * @param exprTerm    the expression term to splat.
   * @param leftSquare  the left square node.
   * @param star        the star.
   * @param rightSquare the right square node.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclFullSplatNode(
      HclExprTermNode exprTerm,
      HclToken leftSquare,
      HclToken star,
      HclToken rightSquare
  ) implements HclSplatNode {

    @Override
    public HclLocation start() {
      return exprTerm.start();
    }

    @Override
    public HclLocation end() {
      return rightSquare.end();
    }
  }
}
