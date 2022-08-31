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

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Errors that the lexer can emit.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public enum TokenErrorMessage {
  UNRECOGNISED_CHAR("unrecognised character"),
  MALFORMED_ESCAPE_SEQUENCE("malformed escape sequence"),
  UNKNOWN_OPERATOR("unknown operator");

  private final String value;

  TokenErrorMessage(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
