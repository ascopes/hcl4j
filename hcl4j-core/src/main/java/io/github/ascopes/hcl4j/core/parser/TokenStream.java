package io.github.ascopes.hcl4j.core.parser;

import io.github.ascopes.hcl4j.core.lexer.Lexer;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

public final class TokenStream {

  private final Lexer lexer;
  private final LinkedList<Token> lookAhead;

  public TokenStream(Lexer lexer) {
    this.lexer = lexer;
    lookAhead = new LinkedList<>();
  }

  public Token peek(int offset) throws IOException, IllegalArgumentException {
    if (offset < 0) {
      throw new IllegalArgumentException("Cannot peek at an offset less than 0");
    }

    for (var i = lookAhead.size(); i < offset; ++i) {
      // TODO(ascopes): consider adding a mechanism to stop at EOF and check this explicitly
      //   rather than filling the linked list up with lots of EofToken instances.
      lookAhead.push(lexer.nextToken());
    }

    return lookAhead.get(offset);
  }

  public Token eat(TokenType... expectedTypes) throws IOException {

  }
}
