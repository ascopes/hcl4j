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

import java.io.IOException;

/**
 * Exception thrown if any underlying {@link IOException} is thrown while reading or writing files.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclStreamException extends HclProcessingException {

  /**
   * Initialise this exception.
   *
   * @param message the associated error message.
   * @param cause   the {@link IOException} that caused this exception to be thrown.
   */
  public HclStreamException(String message, IOException cause) {
    super(message, cause);
  }

  @Override
  public IOException getCause() {
    return (IOException) super.getCause();
  }
}
