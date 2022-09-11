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

package io.github.ascopes.hcl4j.core.parser;

import io.github.ascopes.hcl4j.core.ex.HclBadTokenException;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.ex.HclStreamException;
import io.github.ascopes.hcl4j.core.ex.HclUnexpectedTokenException;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.io.IOException;

/**
 * A stream of tokens that supports arbitrary look-ahead.
 *
 * <p>Parsers using this interface will be capable of {@code LL(k)} look-ahead.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface HclTokenStream {

  /**
   * Get the current location of the token stream.
   *
   * @return the current location.
   */
  HclLocation location();

  /**
   * Peek at the token at the given offset from the current position without advancing the token
   * stream state.
   *
   * @param offset the offset (greater or equal to 0) to peek at.
   * @return the token we peeked at.
   * @throws HclProcessingException if an unexpected exception occurs.
   */
  HclToken peek(int offset) throws HclProcessingException;

  /**
   * Attempt to eat the next token, assuming it is one of the given types.
   *
   * <p>If the token matches one of the types, then it is returned and the stream state
   * is marked as having advanced ready for the next token.
   *
   * @param type  the first token type to expect.
   * @param types any additional token types to expect.
   * @return the next token.
   * @throws HclStreamException          if the input stream cannot be read due to an internal
   *                                     {@link IOException}.
   * @throws HclBadTokenException        if the next token is unable to be tokenized to a known
   *                                     token type (e.g. a malformed input is consumed).
   * @throws HclUnexpectedTokenException if the next token does not match any of the given types.
   */
  HclToken eat(HclTokenType type, HclTokenType... types) throws HclProcessingException;
}
