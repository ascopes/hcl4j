/*
 * Copyright (C) 2022 Ashley Scopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ascopes.hcl4j.lexer.test;

import io.github.ascopes.hcl4j.core.inputs.CharInputStream;
import io.github.ascopes.hcl4j.core.lexer.ConfigLexerStrategy;
import io.github.ascopes.hcl4j.core.lexer.LexerContext;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class StringTest {

  @Test
  void testString() throws IOException {
    var source = """
            foo = "\\n\\nhi\\n\\n";
        """.stripIndent();

    try (
        var in = new CharInputStream("example.hcl", new ByteArrayInputStream(source.getBytes()))
    ) {
      var lex = new LexerContext(in, ConfigLexerStrategy::new);

      Token next;

      do {
        next = lex.nextToken();
        System.out.println(next);
      } while (next.type() != TokenType.END_OF_FILE);
    }
  }
}
