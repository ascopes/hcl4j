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

package io.github.ascopes.hcl4j.core.tokens.impl;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Token that represents that the end of the file has been reached.
 *
 * @param location the location of the end-of-file marker.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public record EofToken(Location location) implements Token {

  @Override
  public TokenType type() {
    return TokenType.END_OF_FILE;
  }

  @Override
  public CharSequence raw() {
    return "\0";
  }
}