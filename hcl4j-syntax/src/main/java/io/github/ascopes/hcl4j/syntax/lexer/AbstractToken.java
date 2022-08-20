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

public abstract class AbstractToken implements Token {

  private final Location location;
  private final CharSequence raw;

  protected AbstractToken(Location location, CharSequence raw) {
    this.location = location;
    this.raw = raw;
  }

  protected AbstractToken(Location location, int rawChar) {
    this(location, Character.toString(rawChar));
  }

  @Override
  public abstract String toString();

  public Location getLocation() {
    return location;
  }

  public CharSequence getRaw() {
    return raw;
  }
}
