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

package io.github.ascopes.hcl4j.core.inputs;

/**
 * Definition of a range between two locations.
 *
 * @param start the start location.
 * @param end   the end location, (exclusive).
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record Range(Location start, Location end) {

  /**
   * Get the number of lines the range spans across.
   *
   * <p>This will always be at least 1.
   *
   * @return the number of lines the range occurs in.
   */
  public long lines() {
    return 1 + (end.line() - start.line());
  }

  /**
   * Get the number of characters the range spans across.
   *
   * @return the number of characters the range spans across.
   */
  public long chars() {
    return end.position() - start.position();
  }
}
