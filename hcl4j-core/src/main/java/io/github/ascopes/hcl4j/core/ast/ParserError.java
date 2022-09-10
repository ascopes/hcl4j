package io.github.ascopes.hcl4j.core.ast;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.util.EnumSet;

/**
 * Nodes that get returned if an error occurs during parsing.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface ParserError extends Node {

  /**
   * Get the first token that is associated with the error.
   *
   * @return the first token associated with the error.
   */
  Token token();

  /**
   * Get the associated error message.
   *
   * @return the associated error message.
   */
  String message();

  /**
   * Node returned when a token fails to be created.
   *
   * @param token the malformed token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record MalformdTokenError(
      @Override ErrorToken token
  ) implements ParserError {

    @Override
    public String message() {
      return token.errorMessage().toString();
    }

    @Override
    public Location start() {
      return token.start();
    }

    @Override
    public Location end() {
      return token.end();
    }
  }

  /**
   * Node returned when parsing fails due to an unexpected token appearing.
   *
   * @param token the malformed token.
   * @author Ashley Scopes
   * @since 0.0.1
   */
  record UnexpectedTokenError(
      @Override Token token,
      EnumSet<TokenType> expectedTypes
  ) implements ParserError {

    @Override
    public String message() {
      var builder = new StringBuilder()
          .append("Unexpected token ")
          .append(token.type().displayName())


      if (expectedTypes.size() > 1) {
        builder.append("one of ");
      }

      var first = true;
      for (var expectedType : expectedTypes) {
        if (first) {
          first = false;
        } else {
          builder.append(", ");
        }

        builder.append(expectedType.symbol());
      }

      return builder.toString();
    }

    @Override
    public Location start() {
      return token.start();
    }

    @Override
    public Location end() {
      return token.end();
    }
  }
}
