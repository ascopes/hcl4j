package io.github.ascopes.hcl4j.core.lexer;

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

/**
 * Wrapper around a string builder that handles common mistakes with int return values.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@SuppressWarnings("UnusedReturnValue")
public final class RawTokenBuilder {

  private final StringBuilder builder;

  /**
   * Initialize the builder.
   */
  public RawTokenBuilder() {
    builder = new StringBuilder();
  }

  /**
   * Append an integer code point.
   *
   * @param codePoint the code point to append.
   * @return this object.
   */
  public RawTokenBuilder append(int codePoint) {
    if (codePoint == EOF) {
      throw new IllegalStateException("Unexpected EOF");
    }

    if (codePoint < 0) {
      throw new IllegalArgumentException("Unexpected codepoint " + codePoint);
    }

    builder.append((char) codePoint);

    return this;
  }

  /**
   * Append a character sequence.
   *
   * @param string the string to append.
   * @return this object.
   */
  public RawTokenBuilder append(CharSequence string) {
    builder.append(string);
    return this;
  }

  /**
   * Convert the builder content to a string and return it.
   *
   * @return the raw content as a string.
   */
  public CharSequence raw() {
    return builder.toString();
  }
}
