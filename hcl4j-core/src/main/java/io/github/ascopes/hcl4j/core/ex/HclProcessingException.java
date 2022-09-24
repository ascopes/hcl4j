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

/**
 * Base for any exception raised when something goes wrong with the processing of HCL content.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class HclProcessingException extends HclException {

  /**
   * Initialise this exception.
   *
   * @param message the exception message.
   */
  protected HclProcessingException(String message) {
    super(message);
  }

  /**
   * Initialise this exception.
   *
   * @param message the exception message.
   * @param cause   the exception cause.
   */
  protected HclProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
