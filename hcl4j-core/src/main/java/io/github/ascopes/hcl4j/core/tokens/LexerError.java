package io.github.ascopes.hcl4j.core.tokens;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Errors that the lexer can emit.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public enum LexerError {
  UNRECOGNISED_CHAR("unrecognised character"),
  MALFORMED_ESCAPE_SEQUENCE("malformed escape sequence"),
  UNKNOWN_OPERATOR("unknown operator");

  private final String value;

  LexerError(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
