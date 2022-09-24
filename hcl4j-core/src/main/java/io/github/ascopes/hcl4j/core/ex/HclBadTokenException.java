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

/**
 * Exception thrown if the lexer comes across some chunk of text that it cannot make sense of, such
 * as an unknown operator, illegal escape sequence, or unexpected text sequence.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclBadTokenException extends HclSyntaxException {

  private final HclLocation start;
  private final HclLocation end;

  private final CharSequence rawContent;

  /**
   * Initialize the exception.
   *
   * @param fileName   the name of the file that produced the exception.
   * @param rawContent the raw content that produced the exception.
   * @param start      the start position of the raw content.
   * @param end        the end position of the raw content.
   * @param message    the error message to use.
   */
  public HclBadTokenException(
      String fileName,
      CharSequence rawContent,
      HclLocation start,
      HclLocation end,
      String message
  ) {
    super(fileName, message);
    this.start = start;
    this.end = end;
    this.rawContent = rawContent;
  }

  /**
   * Initialize the exception.
   *
   * @param fileName   the name of the file that produced the exception.
   * @param rawContent the raw content that produced the exception.
   * @param start      the start position of the raw content.
   * @param end        the end position of the raw content.
   * @param message    the error message to use.
   * @param cause      the cause of the exception.
   */
  public HclBadTokenException(
      String fileName,
      CharSequence rawContent,
      HclLocation start,
      HclLocation end,
      String message,
      Throwable cause
  ) {
    super(fileName, message, cause);
    this.start = start;
    this.end = end;
    this.rawContent = rawContent;
  }

  @Override
  public HclLocation getStart() {
    return start;
  }

  @Override
  public HclLocation getEnd() {
    return end;
  }

  @Override
  public CharSequence getRawContent() {
    return rawContent;
  }
}
