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

/**
 * Additional identifiers that follow the first identifier in a {@code for} expression.
 *
 * @param commaToken the commaToken token.
 * @param identifier the identifier.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record AdditionalForIdentifier(Token commaToken, Identifier identifier) implements Node {

  @Override
  public Location start() {
    return commaToken.start();
  }

  @Override
  public Location end() {
    return identifier.end();
  }
}
