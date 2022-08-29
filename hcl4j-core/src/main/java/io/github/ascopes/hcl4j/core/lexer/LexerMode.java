package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.tokens.Token;
import java.io.IOException;

/**
 * Base interface for an active lexer mode.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface LexerMode {

  /**
   * Get the next token in the file.
   *
   * @return the next token.
   * @throws IOException if an {@link IOException} occurs internally while reading the input
   *                     source.
   */
  Token nextToken() throws IOException;
}
