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
import io.github.ascopes.hcl4j.core.utils.HclTextUtils;
import io.github.ascopes.hcl4j.core.utils.ToStringBuilder;

/**
 * Token that is returned if a tokenization error occurs due to unexpected input sequences.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class ErrorToken extends AbstractToken {

  private final ErrorType errorType;

  private final String errorDetail;

  public ErrorToken(Location location, ErrorType errorType, String errorDetail, String raw) {
    super(location, raw);
    this.errorType = errorType;
    this.errorDetail = errorDetail;
  }

  public ErrorToken(Location location, ErrorType errorType, String errorDetail, int... rawChars) {
    super(location, HclTextUtils.join(rawChars));
    this.errorType = errorType;
    this.errorDetail = errorDetail;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .add("location", getLocation())
        .add("errorType", errorType)
        .add("errorDetail", errorDetail)
        .toString();
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public String getErrorDetail() {
    return errorDetail;
  }

  public enum ErrorType {
    UNEXPECTED_CHARACTER,
    MALFORMED_NEWLINE_SEQUENCE,
    UNKNOWN_OPERATOR,
    MALFORMED_OPENING_HEREDOC_ANCHOR,
    MALFORMED_CLOSING_HEREDOC_ANCHOR,
    MALFORMED_NUMBER_LITERAL,
    UNEXPECTED_END_OF_FILE,
    MALFORMED_UNICODE_ESCAPE_SEQUENCE,
  }
}
