package io.github.ascopes.hcl4j.core.lexer.test;

import io.github.ascopes.hcl4j.core.lexer.CharSource;
import io.github.ascopes.hcl4j.core.lexer.Lexer;
import io.github.ascopes.hcl4j.core.tokens.EofToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class TerraformTest {

  @Test
  void terraformTest() throws IOException {
    try (var is = getClass().getResourceAsStream("/example.tf")) {
      var source = new CharSource("example.tf", is);
      var lexer = new Lexer(source);

      var i = 0;
      Token token;

      try {
        do {
          var mode = lexer.getMode();
          token = lexer.nextToken();
          ++i;
          System.out.printf("Token #%d: %s (mode = %s)%n", i, token, mode);
        } while (!(token instanceof EofToken));
      } finally {
        System.out.printf("Final mode: %s%n", lexer.getMode());
      }
    }
  }

}
