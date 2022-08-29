package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.inputs.Location;

/**
 * Implementation of token that represents an error.
 *
 * @param errorMessage the error message.
 * @param raw          the raw content that triggered the error.
 * @param location     the location in the file that the error occurred at.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record ErrorToken(
    LexerError errorMessage,
    CharSequence raw,
    Location location
) implements Token {

  @Override
  public TokenType type() {
    // Always "error".
    return TokenType.ERROR;
  }
}
