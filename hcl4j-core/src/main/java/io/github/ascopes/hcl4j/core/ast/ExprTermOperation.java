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

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.util.List;

/**
 * Valid types of operations performed upon nested expression terms.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface ExprTermOperation extends ExprTerm {

  /**
   * A sealed marker interface that marks valid nodes that can be used within the splat of a
   * {@link Splat}.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface SplatTerm extends Node {}

  /**
   * Base interface for "splat" operations.
   *
   * @author Ashley Scopes
   * @since 0.0.1
   */
  sealed interface Splat extends ExprTermOperation {}

  /**
   * An index operator term.
   *
   * @param leftToken  the left square bracket token.
   * @param index      the index being extracted from the term.
   * @param rightToken the left square bracket token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record Index(
      Token leftToken,
      Expression index,
      Token rightToken
  ) implements ExprTermOperation, SplatTerm {

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
   * A legacy index operator term. This uses the get-attr syntax but with an integer index.
   *
   * @param dotToken the dot token.
   * @param index    the index being extracted from the term.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record LegacyIndex(
      Token dotToken,
      Expression index
  ) implements ExprTermOperation, SplatTerm {

    @Override
    public Location start() {
      return dotToken.start();
    }

    @Override
    public Location end() {
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
  record GetAttr(
      Token dotToken,
      Identifier identifier
  ) implements ExprTermOperation, SplatTerm {

    @Override
    public Location start() {
      return dotToken.start();
    }

    @Override
    public Location end() {
      return identifier.end();
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
  record AttrSplat(
      Token dotToken,
      Token starToken,
      List<GetAttr> splatTerms
  ) implements Splat {

    @Override
    public Location start() {
      return dotToken.start();
    }

    @Override
    public Location end() {
      return splatTerms.isEmpty()
          ? starToken.end()
          : splatTerms.get(splatTerms.size() - 1).end();
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
  record FullSplat(
      Token leftToken,
      Token starToken,
      Token rightToken,
      List<? extends SplatTerm> splatTerms
  ) implements Splat {

    @Override
    public Location start() {
      return leftToken.start();
    }

    @Override
    public Location end() {
      return splatTerms.isEmpty()
          ? rightToken.end()
          : splatTerms.get(splatTerms.size() - 1).end();
    }
  }
}
