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

import io.github.ascopes.hcl4j.core.inputs.Location;

/**
 * Exception thrown when malformed syntax is found in an input file.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public class HclSyntaxException extends HclProcessingException {

  private final String fileName;
  private final CharSequence rawContent;
  private final Location startLocation;
  private final Location endLocation;

  /**
   * Initialise this exception.
   *
   * @param fileName      the file name that contained erroneous content.
   * @param rawContent    the erroneous content.
   * @param startLocation the start location of the erroneous content.
   * @param endLocation   the end location of the erroneous content.
   * @param message       the exception message.
   */
  public HclSyntaxException(
      String fileName,
      CharSequence rawContent,
      Location startLocation,
      Location endLocation,
      String message
  ) {
    this(fileName, rawContent, startLocation, endLocation, message, null);
  }

  /**
   * Initialise this exception.
   *
   * @param fileName      the file name that contained erroneous content.
   * @param rawContent    the erroneous content.
   * @param startLocation the start location of the erroneous content.
   * @param endLocation   the end location of the erroneous content.
   * @param message       the exception message.
   * @param cause         the cause of this exception.
   */
  public HclSyntaxException(
      String fileName,
      CharSequence rawContent,
      Location startLocation,
      Location endLocation,
      String message,
      Throwable cause
  ) {
    super(message, cause);
    this.fileName = fileName;
    this.rawContent = rawContent;
    this.startLocation = startLocation;
    this.endLocation = endLocation;
  }

  /**
   * Get the file name that contained the erroneous content.
   *
   * @return the file name.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Get the raw erroneous content.
   *
   * @return the content.
   */
  public CharSequence getRawContent() {
    return rawContent;
  }

  /**
   * Get the start location of the erroneous content in the file.
   *
   * @return the start location of the erroneous content in the file.
   */
  public Location getStartLocation() {
    return startLocation;
  }

  /**
   * Get the end location of the erroneous content in the file.
   *
   * @return the end location of the erroneous content in the file.
   */
  public Location getEndLocation() {
    return endLocation;
  }

  @Override
  public String toString() {
    return "Error occurred for "
        + safeRepr(rawContent)
        + " in "
        + fileName
        + " at line " + startLocation.line()
        + ", column " + startLocation.column();
  }
}
