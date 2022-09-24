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
package io.github.ascopes.hcl4j.core.inputs;

import io.github.ascopes.hcl4j.core.ex.HclStreamException;
import java.io.IOException;

/**
 * A character stream for reading an HCL source file.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface HclCharSource extends AutoCloseable {

  /**
   * Marker to represent that the end of the file has been reached when calling {@link #peek}.
   */
  int EOF = -1;

  /**
   * Default name for an unnamed file.
   */
  String UNNAMED_FILE = "<unnamed>";

  /**
   * Advance the current location forwards by {@code count} characters.
   *
   * @param count the number of characters to advance the position by.
   * @throws HclStreamException if an {@link IOException} is thrown internally.
   */
  void advance(int count) throws HclStreamException;

  /**
   * Close the internal resource providing data to this stream.
   *
   * @throws HclStreamException if an {@link IOException} is thrown internally.
   */
  @Override
  void close() throws HclStreamException;

  /**
   * Create a new immutable object representing the current location in the file.
   *
   * @return the object representing the file location.
   */
  HclLocation location();

  /**
   * Get the name of the file.
   *
   * <p>This may be {@link #UNNAMED_FILE} if no name was provided in the implementation.
   *
   * @return the name of the file.
   */
  String name();

  /**
   * Peek at the character codepoint at the given {@code offset} from the current position.
   *
   * <p>This will not advance the current position.
   *
   * @param offset the offset from the current location to peek.
   * @return the character codepoint, or {@link #EOF}.
   * @throws HclStreamException if an {@link IOException} is thrown internally.
   */
  int peek(int offset) throws HclStreamException;

  /**
   * Read the next character and return it.
   *
   * <p>This will advance the current position.
   *
   * @return the character codepoint, or {@link #EOF}.
   * @throws HclStreamException if an {@link IOException} is thrown internally.
   */
  int read() throws HclStreamException;

  /**
   * Read up to {@code count} characters from the current position in the stream and return them
   * within a {@link CharSequence}.
   *
   * <p>If the end of the file is reached prematurely, the returned sequence will be shorter. This
   * may even possibly be zero length. Thus, it is important to have either {@link #peek peeked} the
   * characters first, or to check the returned value from this method explicitly.
   *
   * <p>Since this returns a buffer of characters internally, this method should be avoided if
   * the returned contents are to be discarded immediately after checking their value. Opt for
   * {@link #peek peeking} each character individually instead. This method is designed to provide
   * longer-lived buffers of characters to place within tokens.
   *
   * <p>In addition, this method will <strong>advance</strong> the current position.
   *
   * @param count the number of characters to read.
   * @return the read character sequence.
   * @throws HclStreamException if an {@link IOException} is thrown internally.
   */
  CharSequence readString(int count) throws HclStreamException;

  /**
   * Determine if the next characters in the input stream match the given character sequence.
   *
   * <p>This will not advance the current position.
   *
   * @param match the character sequence to match.
   * @return {@code true} if the character sequence occurs at the current location in the input, or
   *     {@code false} if it does not.
   * @throws HclStreamException if an {@link IOException} is thrown internally.
   */
  boolean startsWith(CharSequence match) throws HclStreamException;
}
