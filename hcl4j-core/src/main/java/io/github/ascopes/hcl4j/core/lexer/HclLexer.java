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

package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.ex.HclStreamException;
import io.github.ascopes.hcl4j.core.ex.HclSyntaxException;
import io.github.ascopes.hcl4j.core.inputs.HclCharSource;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Base interface for an HCL lexer.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface HclLexer {

  /**
   * Get the character source for the lexer.
   *
   * @return the character source.
   */
  HclCharSource charSource();

  /**
   * Push a new strategy onto the lexer strategy stack.
   *
   * @param mode the lexer strategy to push.
   */
  void pushStrategy(HclLexerStrategy mode);

  /**
   * Pop a strategy from the lexer strategy stack.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  void popStrategy() throws NoSuchElementException;

  /**
   * Retrieve the next token. If the stack is empty, a {@link HclTokenType#END_OF_FILE} token will
   * be returned as default behaviour.
   *
   * @throws HclStreamException if an {@link IOException} occurs internally while reading the input
   *                            source.
   * @throws HclSyntaxException if the next token is unknown or fails to be consumed.
   */
  HclToken nextToken() throws HclProcessingException;
}
