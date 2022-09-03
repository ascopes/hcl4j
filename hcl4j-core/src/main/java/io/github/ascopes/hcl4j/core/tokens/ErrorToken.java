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

import io.github.ascopes.hcl4j.core.inputs.Location;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Implementation of token that represents an error.
 *
 * @param errorMessage the error message.
 * @param raw          the raw content that triggered the error.
 * @param location     the location in the file that the error occurred at.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public record ErrorToken(
    TokenErrorMessage errorMessage,
    CharSequence raw,
    Location location
) implements Token {

  @Override
  public TokenType type() {
    // Always "error".
    return TokenType.WTF;
  }
}
