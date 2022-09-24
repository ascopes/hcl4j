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
package io.github.ascopes.hcl4j.core.intern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Wrapper around an {@link InputStream} that will handle a UTF-8 byte-order mark if one is present
 * at the start. This also ensures any reading operations are backed by an in-memory buffer for
 * efficiency.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class BufferedUtf8BomReader extends BufferedReader {

  private static final char UTF_8_BOM = 0xFE_FF;

  /**
   * Initialize the reader from the input stream using UTF-8 encoding, and consume a leading
   * byte-order mark if one is present.
   *
   * @param inputStream the input stream.
   * @throws IOException if an exception occurred while reading the first three bytes.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public BufferedUtf8BomReader(InputStream inputStream) throws IOException {
    super(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    mark(1);
    var firstChar = read();
    reset();

    // Skip the byte-order mark for UTF-8 if present.
    if (UTF_8_BOM == firstChar) {
      skip(1);
    }
  }
}
