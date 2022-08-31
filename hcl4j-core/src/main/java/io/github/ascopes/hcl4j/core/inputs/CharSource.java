package io.github.ascopes.hcl4j.core.inputs;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * A character stream for reading an HCL source file.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public interface CharSource extends AutoCloseable {

  /**
   * The charset. This is always UTF-8 for HCL source files.
   */
  Charset CHARSET = StandardCharsets.UTF_8;

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
   * @throws IOException if an {@link IOException} is thrown internally.
   */
  void advance(int count) throws IOException;

  /**
   * Close the internal resource providing data to this stream.
   *
   * @throws IOException if an {@link IOException} is thrown internally.
   */
  @Override
  void close() throws IOException;

  /**
   * Create a new immutable object representing the current location in the file.
   *
   * @return the object representing the file location.
   */
  @CheckReturnValue
  Location location();

  /**
   * Get the name of the file.
   *
   * <p>This may be {@link #UNNAMED_FILE} if no name was provided in the implementation.
   *
   * @return the name of the file.
   */
  @CheckReturnValue
  String name();

  /**
   * Peek at the character codepoint at the given {@code offset} from the current position.
   *
   * <p>This will not advance the current position.
   *
   * @param offset the offset from the current location to peek.
   * @return the character codepoint, or {@link #EOF}.
   * @throws IOException if an {@link IOException} is thrown internally.
   */
  @CheckReturnValue
  int peek(int offset) throws IOException;

  /**
   * Read the next character and return it.
   *
   * <p>This will advance the current position.
   *
   * @return the character codepoint, or {@link #EOF}.
   * @throws IOException if an {@link IOException} is thrown internally.
   */
  @CheckReturnValue
  int read() throws IOException;

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
   * @throws IOException if an {@link IOException} is thrown internally.
   */
  @CheckReturnValue
  CharSequence readString(int count) throws IOException;

  /**
   * Determine if the next characters in the input stream match the given character sequence.
   *
   * <p>This will not advance the current position.
   *
   * @param match the character sequence to match.
   * @return {@code true} if the character sequence occurs at the current location in the input, or
   * {@code false} if it does not.
   * @throws IOException if an {@link IOException} is thrown internally.
   */
  @CheckReturnValue
  boolean startsWith(CharSequence match) throws IOException;
}
