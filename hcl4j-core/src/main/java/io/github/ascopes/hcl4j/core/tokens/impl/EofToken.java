package io.github.ascopes.hcl4j.core.tokens.impl;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Token that represents that the end of the file has been reached.
 *
 * @param location the location of the end-of-file marker.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public record EofToken(Location location) implements Token {

  @Override
  public TokenType type() {
    return TokenType.END_OF_FILE;
  }

  @Override
  public CharSequence raw() {
    return "\0";
  }
}
