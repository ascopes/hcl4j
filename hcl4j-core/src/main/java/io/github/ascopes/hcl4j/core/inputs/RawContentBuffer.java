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

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

import io.github.ascopes.hcl4j.core.annotations.Api;
import io.github.ascopes.hcl4j.core.annotations.Api.Visibility;
import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;

/**
 * Wrapper around a string builder that handles common mistakes with int return values.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Api(Visibility.INTERNAL)
@SuppressWarnings("UnusedReturnValue")
public final class RawContentBuffer {

  private final StringBuilder builder;

  /**
   * Initialize the buffer.
   */
  public RawContentBuffer() {
    builder = new StringBuilder();
  }

  /**
   * Append an integer code point.
   *
   * @param codePoint the code point to append.
   * @return this object.
   */
  public RawContentBuffer append(int codePoint) {
    if (codePoint == EOF) {
      throw new IllegalStateException("Unexpected EOF");
    }

    builder.appendCodePoint(codePoint);

    return this;
  }

  /**
   * Append a character sequence.
   *
   * @param string the string to append.
   * @return this object.
   */
  public RawContentBuffer append(CharSequence string) {
    builder.append(string);
    return this;
  }

  /**
   * Convert a string hexadecimal codepoint (no prefix) to UTF-8 code points and append them to the
   * buffer.
   *
   * @param hex the hexadecimal string to parse, e.g. {@code "1a2b3c"}.
   * @return this object.
   * @throws NumberFormatException    if the given string is not a valid hexadecimal integer.
   * @throws IllegalArgumentException if the given string is not a valid unicode codepoint.
   */
  public RawContentBuffer appendHexCodePoint(CharSequence hex) {
    var codePoint = Integer.parseInt(hex.toString(), 16);
    builder.append(Character.toChars(codePoint));
    return this;
  }

  /**
   * Convert the builder content to a string and return it.
   *
   * @return the raw content as a string.
   */
  @CheckReturnValue
  public CharSequence content() {
    return builder.toString();
  }

  /**
   * Convert the builder content to a string and return it.
   *
   * @return the raw content as a string.
   * @deprecated use {@link #content()} instead of this method.
   */
  @CheckReturnValue
  @Deprecated
  @SuppressWarnings("DeprecatedStillUsed")
  public String toString() {
    return builder.toString();
  }
}
