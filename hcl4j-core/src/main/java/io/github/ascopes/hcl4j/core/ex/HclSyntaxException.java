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

/**
 * Base type for an exception that can be thrown due to malformed syntax in an input file.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class HclSyntaxException extends HclProcessingException {

  private final String fileName;

  /**
   * Initialize the exception.
   *
   * @param fileName the name of the file that produced the exception.
   * @param message  the error message to use.
   */
  protected HclSyntaxException(String fileName, String message) {
    super(message);
    this.fileName = fileName;
  }

  /**
   * Initialize the exception.
   *
   * @param fileName the name of the file that produced the exception.
   * @param message  the error message to use.
   * @param cause    the cause of the exception.
   */
  protected HclSyntaxException(String fileName, String message, Throwable cause) {
    super(message, cause);
    this.fileName = fileName;
  }

  /**
   * Get the name of the file that produced this exception.
   *
   * @return the file name.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Get the start position of the invalid content that produced this exception.
   *
   * @return the start position.
   */
  public abstract HclLocation getStart();

  /**
   * Get the end position of the invalid content that produced this exception.
   *
   * @return the end position.
   */
  public abstract HclLocation getEnd();

  /**
   * Get the raw content that produced this exception.
   *
   * @return the raw content.
   */
  public abstract CharSequence getRawContent();
}
