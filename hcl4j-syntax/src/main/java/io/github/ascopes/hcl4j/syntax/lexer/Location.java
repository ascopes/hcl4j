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
 * An immutable representation of the position in a given source file.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class Location {
  private final String name;
  private final long position;
  private final long line;
  private final long column;

  public Location(String name, long position, long line, long column) {
    this.name = name;
    this.position = position;
    this.line = line;
    this.column = column;
  }

  public String getName() {
    return name;
  }

  public long getPosition() {
    return position;
  }

  public long getLine() {
    return line;
  }

  public long getColumn() {
    return column;
  }

  @Override
  public String toString() {
    return name + "#L" + line + "." + column;
  }
}
