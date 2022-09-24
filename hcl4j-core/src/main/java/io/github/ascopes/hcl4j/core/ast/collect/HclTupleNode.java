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
package io.github.ascopes.hcl4j.core.ast.collect;

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.util.List;

/**
 * A tuple value literal.
 *
 * @param leftToken    the opening square bracket token.
 * @param elements     the tuple members.
 * @param trailerComma the trailing comma if present and at least one element exists, else
 *                     {@code null}.
 * @param rightToken   the closing square bracket token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTupleNode(
    HclToken leftToken,
    List<HclTupleElementNode> elements,
    @Nullable HclToken trailerComma,
    HclToken rightToken
) implements HclCollectionValueNode {

  @Override
  public HclLocation start() {
    return leftToken.start();
  }

  @Override
  public HclLocation end() {
    return rightToken.end();
  }
}
