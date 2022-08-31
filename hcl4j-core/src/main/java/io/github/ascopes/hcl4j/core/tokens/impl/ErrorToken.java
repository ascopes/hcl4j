package io.github.ascopes.hcl4j.core.tokens.impl;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.LexerError;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Implementation of token that represents an error.
 *
 * @param errorMessage the error message.
 * @param raw          the raw content that triggered the error.
 * @param location     the location in the file that the error occurred at.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public record ErrorToken(
    LexerError errorMessage,
    CharSequence raw,
    Location location
) implements Token {

  @Override
  public TokenType type() {
    // Always "error".
    return TokenType.WTF;
  }
}
