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

package io.github.ascopes.hcl4j.core.ex;

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.util.Collections;
import java.util.Set;

/**
 * Exception thrown if the parser comes across a token that it does not expect during parsing.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclUnexpectedTokenException extends HclSyntaxException {

  private final HclToken unexpectedToken;
  private final Set<HclTokenType> expectedTypes;

  /**
   * Initialize this exception.
   *
   * @param unexpectedToken the unexpected token.
   * @param expectedTypes   the expected types of token that should have been provided instead.
   * @param fileName        the file name that contains the invalid token.
   * @param message         the error message.
   */
  public HclUnexpectedTokenException(
      HclToken unexpectedToken,
      Set<HclTokenType> expectedTypes,
      String fileName,
      String message
  ) {
    super(fileName, message);
    this.unexpectedToken = unexpectedToken;
    this.expectedTypes = Collections.unmodifiableSet(expectedTypes);
  }

  /**
   * Get the unexpected token.
   *
   * @return the unexpected token.
   */
  public HclToken getUnexpectedToken() {
    return unexpectedToken;
  }

  /**
   * Get the expected types of token that should have been provided instead (as an unmodifiable
   * set).
   *
   * @return the expected types of token.
   */
  public Set<HclTokenType> getExpectedTypes() {
    return expectedTypes;
  }

  @Override
  public HclLocation getStart() {
    return unexpectedToken.start();
  }

  @Override
  public HclLocation getEnd() {
    return unexpectedToken.end();
  }

  @Override
  public CharSequence getRawContent() {
    return unexpectedToken.content();
  }
}
