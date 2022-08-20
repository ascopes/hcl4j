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

package io.github.ascopes.hcl4j.syntax.lexer;

/**
 * Token that is returned if a tokenization error occurs due to unexpected input sequences.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class ErrorToken extends AbstractToken {
  private final ErrorType errorType;
  private final String errorDetail;

  public ErrorToken(Location location, ErrorType errorType, String errorDetail, CharSequence raw) {
    super(location, raw);
    this.errorType = errorType;
    this.errorDetail = errorDetail;
  }

  public ErrorToken(Location location, ErrorType errorType, String errorDetail, int... rawChars) {
    super(location, TextUtils.join(rawChars));
    this.errorType = errorType;
    this.errorDetail = errorDetail;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{"
        + "type=" + errorType + ", "
        + "detail=\"" + TextUtils.escapeTextForDiagnostics(errorDetail) + "\", "
        + "location=\"" + getLocation() + "\""
        + "}";
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public String getErrorMessage() {
    return errorType.name().replace('_', ' ');
  }

  public String getErrorDetail() {
    return errorDetail;
  }

  public enum ErrorType {
    UNEXPECTED_CHARACTER,
    MALFORMED_NEWLINE_SEQUENCE,
    UNKNOWN_OPERATOR,
    MALFORMED_HEREDOC_ANCHOR,
    MALFORMED_NUMBER_LITERAL
  }
}
