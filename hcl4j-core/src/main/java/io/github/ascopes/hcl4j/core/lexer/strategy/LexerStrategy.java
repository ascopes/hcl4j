package io.github.ascopes.hcl4j.core.lexer.strategy;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.io.IOException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Base interface for an active lexer mode.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public interface LexerStrategy {

  /**
   * Get the next token in the file.
   *
   * @return the next token.
   * @throws IOException if an {@link IOException} occurs internally while reading the input
   *                     source.
   */
  @CheckReturnValue
  Token nextToken() throws IOException;
}
