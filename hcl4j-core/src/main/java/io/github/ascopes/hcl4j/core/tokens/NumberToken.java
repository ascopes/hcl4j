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
import java.math.BigDecimal;

public final class NumberToken extends AbstractToken {

  private final BigDecimal number;

  public NumberToken(Location location, BigDecimal number, String raw) {
    super(location, raw);
    this.number = number;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .add("location", getLocation())
        .add("number", number)
        .toString();
  }

  public BigDecimal getNumber() {
    return number;
  }
}
