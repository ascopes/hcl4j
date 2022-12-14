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
package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.ex.HclStreamException;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.io.IOException;

/**
 * Base interface for an active lexer mode.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface HclLexerStrategy {

  /**
   * Get the next token in the file.
   *
   * @return the next token.
   * @throws HclStreamException if an {@link IOException} occurs internally while reading the input
   *                            source.
   */
  HclToken nextToken() throws HclStreamException;
}
