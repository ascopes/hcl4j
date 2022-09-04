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

import io.github.ascopes.hcl4j.core.inputs.Range;

/**
 * Abstract representation of a token emitted by a lexer mode.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface Token permits EofToken, ErrorToken, RawTextToken, SimpleToken {

  /**
   * Get the token type.
   *
   * @return the token type.
   */
  TokenType type();

  /**
   * Get the raw content of the token.
   *
   * @return the raw content.
   */
  CharSequence raw();

  /**
   * Get the range of the given token.
   *
   * @return the location range of the token.
   */
  Range range();
}
