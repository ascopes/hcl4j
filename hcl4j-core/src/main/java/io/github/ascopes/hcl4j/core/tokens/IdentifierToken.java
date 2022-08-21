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

package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.lexer.Location;
import io.github.ascopes.hcl4j.core.utils.ToStringBuilder;

public final class IdentifierToken extends AbstractToken {

  public IdentifierToken(Location location, String raw) {
    super(location, raw);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .add("location", getLocation())
        .add("identifier", getIdentifier())
        .toString();
  }

  public String getIdentifier() {
    return getRaw();
  }

  public boolean isLiteralTrue() {
    return getRaw().equals("true");
  }

  public boolean isLiteralFalse() {
    return getRaw().equals("false");
  }

  public boolean isLiteralNull() {
    return getRaw().equals("null");
  }
}
