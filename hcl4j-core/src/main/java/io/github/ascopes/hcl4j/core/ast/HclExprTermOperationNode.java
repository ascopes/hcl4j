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
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.util.List;

/**
 * Valid types of operations performed upon nested expression terms.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclExprTermOperationNode extends HclExprTermNode {

  /**
   * A sealed marker interface that marks valid nodes that can be used within the splat of a
   * {@link HclSplatNode} or {@link HclLegacySplatNode}.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface HclSplatTermNode extends HclNode {}

  /**
   * An index operator term.
   *
   * @param leftToken  the left square bracket token.
   * @param index      the index being extracted from the term.
   * @param rightToken the left square bracket token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclIndexNode(
      HclToken leftToken,
      HclExpressionNode index,
      HclToken rightToken
  ) implements HclExprTermOperationNode, HclSplatTermNode {

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
   * A legacy index operator term. This uses the get-attr syntax but with an integer index.
   *
   * @param dotToken the dot token.
   * @param index    the index being extracted from the term.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclLegacyIndexNode(
      HclToken dotToken,
      HclExpressionNode index
  ) implements HclExprTermOperationNode, HclSplatTermNode {

    @Override
    public HclLocation start() {
      return dotToken.start();
    }

    @Override
    public HclLocation end() {
      return index.end();
    }
  }

  /**
   * An attribute accessor operator term.
   *
   * @param dotToken   the dot token.
   * @param identifier the identifier being extracted from the term.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclGetAttributeNode(
      HclToken dotToken,
      HclIdentifierLikeNode identifier
  ) implements HclExprTermOperationNode, HclSplatTermNode {

    @Override
    public HclLocation start() {
      return dotToken.start();
    }

    @Override
    public HclLocation end() {
      return identifier.end();
    }
  }

  /**
   * A "full" splat operation.
   *
   * @param leftToken  the left brace token.
   * @param starToken  the star token.
   * @param rightToken the right brace token.
   * @param splatTerms any operations to perform on the value before splatting it.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclSplatNode(
      HclToken leftToken,
      HclToken starToken,
      HclToken rightToken,
      List<? extends HclSplatTermNode> splatTerms
  ) implements HclExprTermOperationNode {

    @Override
    public HclLocation start() {
      return leftToken.start();
    }

    @Override
    public HclLocation end() {
      return splatTerms.isEmpty()
          ? rightToken.end()
          : splatTerms.get(splatTerms.size() - 1).end();
    }
  }

  /**
   * A legacy "attr" splat operation.
   *
   * @param dotToken   the dot token.
   * @param starToken  the star token.
   * @param splatTerms any get-attribute operations to perform on the value before splatting it.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record HclLegacySplatNode(
      HclToken dotToken,
      HclToken starToken,
      List<HclGetAttributeNode> splatTerms
  ) implements HclExprTermOperationNode {

    @Override
    public HclLocation start() {
      return dotToken.start();
    }

    @Override
    public HclLocation end() {
      return splatTerms.isEmpty()
          ? starToken.end()
          : splatTerms.get(splatTerms.size() - 1).end();
    }
  }
}
