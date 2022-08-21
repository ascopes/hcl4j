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

package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.utils.HclTextUtils;
import io.github.ascopes.hcl4j.core.utils.ToStringBuilder;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A character source used by the lexer. This enforces UTF-8 encoding internally, and supports
 * single-character lookahead (i.e. this is compatible with LL(1) lexer implementations).
 *
 * <p>In addition, this can emit immutable {@link Location} objects for use within tokens to
 * describe source file locations, which can be useful within diagnostic contexts. This
 * implementation will track the source file position (assuming the input stream starts at an offset
 * of 0), and will track the offset, line, and column numbers.
 *
 * <p>New lines are considered to be either {@code '\n'} (ASCII line feeds), or {@code '\r\n'}
 * (ASCII carriage-returns followed by ASCII line feeds), per the HCL specification. Anything else,
 * including {@code '\t'} (ASCII horizontal tabs) are considered to be single characters that do not
 * change the current line.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class CharSource implements Closeable {

  private static final long INITIAL_POSITION = 0;
  private static final long INITIAL_LINE = 1;
  private static final long INITIAL_COLUMN = 1;

  // HCL is always UTF-8 encoded!
  private static final Charset HCL_CHARSET = StandardCharsets.UTF_8;

  private final Reader reader;

  private final String name;

  private long position;

  private long line;

  private long column;

  public CharSource(String name, InputStream inputStream) {
    // We always expect an input stream to force the user to not provide content in an unexpected
    // encoding. This is intentional by design.
    // We also assume that buffered readers are always markable.
    reader = new BufferedReader(new InputStreamReader(inputStream, HCL_CHARSET));

    assert reader.markSupported() : "BufferedReader does not support marking! Oh no!";

    this.name = name;
    position = INITIAL_POSITION;
    line = INITIAL_LINE;
    column = INITIAL_COLUMN;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .add("name", name)
        .add("position", position)
        .add("line", line)
        .add("column", column)
        .toString();
  }

  public Location getLocation() {
    return new Location(name, position, line, column);
  }

  public int peek() throws IOException {
    try {
      reader.mark(1);
      return reader.read();
    } finally {
      reader.reset();
    }
  }

  public int take() throws IOException {
    var c = reader.read();

    switch (c) {
      case HclTextUtils.EOF:
        break;

      case HclTextUtils.LF:
        ++position;
        ++line;
        column = INITIAL_COLUMN;
        break;

      default:
        ++position;
        ++column;
        break;
    }

    return c;
  }
}
