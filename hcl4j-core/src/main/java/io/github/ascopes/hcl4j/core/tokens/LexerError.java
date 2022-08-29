package io.github.ascopes.hcl4j.core.tokens;

/**
 * Errors that the lexer can emit.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public enum LexerError {
  UNRECOGNISED_CHAR("unrecognised character"),
  UNKNOWN_OPERATOR("unknown operator"),
  UNEXPECTED_EOF_INLINE_COMMENT("unexpected end of file while parsing inline comment"),
  UNEXPECTED_EOF_HEREDOC_IDENTIFIER("unexpected end of file while parsing heredoc identifier"),
  EXPECTED_HEREDOC_IDENTIFIER("expected a heredoc identifier"),
  EXPECTED_NEW_LINE("expected a new line");

  private final String value;

  LexerError(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
