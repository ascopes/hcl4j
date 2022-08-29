package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.inputs.Location;

/**
 * Abstract representation of a token emitted by a lexer mode.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface Token permits ErrorToken, SimpleToken {

  /**
   * Get the token type.
   *
   * @return the token type.
   */
  TokenType type();

  /**
   * Get the raw content of the token.
   *
   * @return the raw content.
   */
  CharSequence raw();

  /**
   * Get the location of the start of the token in the file it was read from.
   *
   * @return the location of the token.
   */
  Location location();
}
