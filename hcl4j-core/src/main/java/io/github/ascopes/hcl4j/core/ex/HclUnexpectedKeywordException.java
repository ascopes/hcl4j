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
package io.github.ascopes.hcl4j.core.ex;

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception thrown if the parser comes across a token that it does not expect during parsing.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclUnexpectedKeywordException extends HclSyntaxException {

  private final HclToken unexpectedToken;
  private final Set<CharSequence> expectedKeywords;

  /**
   * Initialize this exception.
   *
   * @param unexpectedToken  the unexpected token.
   * @param expectedKeywords the expected keywords of token that should have been provided instead.
   * @param fileName         the file name that contains the invalid token.
   * @param message          the error message.
   */
  public HclUnexpectedKeywordException(
      HclToken unexpectedToken,
      Set<CharSequence> expectedKeywords,
      String fileName,
      String message
  ) {
    super(fileName, message);
    this.unexpectedToken = unexpectedToken;
    this.expectedKeywords = Collections.unmodifiableSet(expectedKeywords);
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
  public Set<CharSequence> getExpectedKeywords() {
    return expectedKeywords;
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

  @Override
  public String toString() {
    var expected = expectedKeywords
        .stream()
        .sorted()
        .map(kw -> " - " + safeRepr(kw))
        .collect(Collectors.joining("\n"));

    return getMessage()
        + "\nExpected one of:\n" + expected
        + "\nReceived:"
        + "\n - " + safeRepr(unexpectedToken.content())
        + "\n\nin " + getFileName() + " at "
        + "line " + unexpectedToken.start().line() + ", "
        + "column: " + unexpectedToken.start().column()
        + "\n";
  }
}
