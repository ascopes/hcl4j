package io.github.ascopes.hcl4j.core.inputs;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.annotations.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

/**
 * An {@link CharSource} that wraps a given {@link InputStream} internally, buffering it and
 * encoding the contents in
 * {@link CharSource#CHARSET the standard character encoding for HCL files}.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class CharInputStream implements CharSource {

  private static final int BUFFER_SIZE = 1_024;

  private static final long INITIAL_POSITION = 0;
  private static final long INITIAL_LINE = 1;
  private static final long INITIAL_COLUMN = 1;

  private final String name;
  private final BufferedReader reader;
  private long position;
  private long line;
  private long column;

  /**
   * Initialize the character source.
   *
   * @param name    the symbolic name of the file that the {@code inputStream} is for.
   * @param inputStream the unbuffered input stream source to use (will be buffered internally).
   */
  public CharInputStream(@Nullable String name, InputStream inputStream) {
    this.name = name == null ? UNNAMED_FILE : name;
    var inputReader = new InputStreamReader(inputStream, CHARSET);
    reader = new BufferedReader(inputReader, BUFFER_SIZE);

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
      switch (reader.read()) {
        case EOF -> {
          return;
        }
        case '\n' -> {
          ++position;
          ++line;
          column = INITIAL_COLUMN;
        }
        default -> {
          ++position;
          ++line;
        }
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
    return new Location(name, position, line, column);
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
    return reader.read();
  }

  @CheckReturnValue
  @Override
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public CharSequence readString(int count) throws IOException {
    if (count < 0) {
      throw new IllegalArgumentException("Cannot read a negative number of characters");
    }

    var charBuffer = CharBuffer.allocate(count);
    reader.read(charBuffer);

    // Shrink the allocated buffer size if we didn't fill the full buffer.
    // If we did, then don't bother compacting it.
    return charBuffer.position() < charBuffer.capacity()
        ? charBuffer.mark().rewind().compact()
        : charBuffer.rewind();
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
}
