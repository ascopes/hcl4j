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

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.annotations.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link CharSource} that wraps a given {@link InputStream} internally, buffering it and
 * handling any leading byte-order mark.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class CharInputStream implements CharSource {

  private static final long INITIAL_POSITION = 0;
  private static final long INITIAL_LINE = 1;
  private static final long INITIAL_COLUMN = 1;

  private final String name;
  private final BufferedReader reader;

  @Nullable
  private Location cachedLocation;

  private long position;
  private long line;
  private long column;

  /**
   * Initialize the character source.
   *
   * @param name        the symbolic name of the file that the {@code inputStream} is for.
   * @param inputStream the unbuffered input stream source to use (will be buffered internally).
   * @throws IOException if an IO error occurs reading the first 3 bytes.
   */
  public CharInputStream(@Nullable String name, InputStream inputStream) throws IOException {
    this.name = name == null ? UNNAMED_FILE : name;
    reader = new BufferedUtf8BomReader(inputStream);

    cachedLocation = null;
    position = INITIAL_POSITION;
    line = INITIAL_LINE;
    column = INITIAL_COLUMN;
  }

  @Override
  public void advance(int count) throws IOException {
    if (count <= 0) {
      throw new IllegalArgumentException("Cannot advance by less than 1 character");
    }

    for (var i = 0; i < count; ++i) {
      var next = reader.read();
      processNextChar(next);
      if (next == EOF) {
        break;
      }
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @CheckReturnValue
  @Override
  public Location location() {
    // Cache the location after making it so that the next token can share the same object
    // reference. It is a small optimisation on the memory footprint.
    return cachedLocation == null
        ? (cachedLocation = new Location(position, line, column))
        : cachedLocation;
  }

  @CheckReturnValue
  @Override
  public String name() {
    return name;
  }

  @CheckReturnValue
  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public int peek(int offset) throws IOException {
    if (offset < 0) {
      throw new IllegalArgumentException("Cannot peek by a negative offset");
    }

    reader.mark(offset + 1);

    try {
      reader.skip(offset);
      return reader.read();
    } finally {
      reader.reset();
    }
  }

  @CheckReturnValue
  @Override
  public int read() throws IOException {
    var next = reader.read();
    processNextChar(next);
    return next;
  }

  @CheckReturnValue
  @Override
  public CharSequence readString(int count) throws IOException {
    if (count < 0) {
      throw new IllegalArgumentException("Cannot read a negative number of characters");
    }

    var buff = new StringBuilder();

    for (var i = 0; i < count; ++i) {
      var next = reader.read();
      processNextChar(next);
      if (next == EOF) {
        break;
      }
      buff.append((char) next);
    }

    return buff.toString();
  }

  @CheckReturnValue
  @Override
  public boolean startsWith(CharSequence match) throws IOException {
    var len = match.length();

    reader.mark(len);

    try {
      for (var i = 0; i < len; ++i) {
        var expect = (int) match.charAt(i);
        var actual = reader.read();

        if (actual != expect) {
          return false;
        }
      }

      return true;
    } finally {
      reader.reset();
    }
  }

  private void processNextChar(int codePoint) {
    switch (codePoint) {
      case EOF -> {
        // Do nothing.
        return;
      }
      case '\n' -> {
        ++line;
        column = INITIAL_COLUMN;
      }
      default -> {
        ++column;
      }
    }

    ++position;

    // Invalidate any cached location, the position is different now.
    cachedLocation = null;
  }
}
