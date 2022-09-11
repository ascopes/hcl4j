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
 * A function call.
 *
 * @param identifier the function name.
 * @param leftToken  the left parenthesis token.
 * @param arguments  the function parameters.
 * @param rightToken the right parenthesis token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclFunctionCallNode(
    HclIdentifierNode identifier,
    HclToken leftToken,
    List<HclParameterNode> arguments,
    HclToken rightToken
) implements HclExprTermNode {

  @Override
  public HclLocation start() {
    return identifier.start();
  }

  @Override
  public HclLocation end() {
    return rightToken.end();
  }

  /**
   * A function call parameter.
   *
   * @param expression the expression for the parameter.
   * @param commaToken the trailing comma token (can be {@code null} for the last parameter).
   * @author Ashley Scopes
   * @since 0.0.1
   */
  public record HclParameterNode(
      HclExpressionNode expression,
      @Nullable HclToken commaToken
  ) implements HclNode {

    @Override
    public HclLocation start() {
      return expression.start();
    }

    @Override
    public HclLocation end() {
      return commaToken == null
          ? expression.end()
          : commaToken.end();
    }
  }
}
