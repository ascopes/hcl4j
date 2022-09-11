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

package io.github.ascopes.hcl4j.core.parser;

import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.ex.HclUnexpectedTokenException;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.lexer.HclLexer;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;

/**
 * A simple wrapper around a lexer that provides useful stream-oriented operations for parsers to
 * consume tokens with.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclSimpleTokenStream implements HclTokenStream {

  private final HclLexer lexer;

  private final LinkedList<HclToken> tokens;

  /**
   * Initialize this stream.
   *
   * @param lexer the lexer to wrap.
   */
  public HclSimpleTokenStream(HclLexer lexer) {
    this.lexer = lexer;
    tokens = new LinkedList<>();
  }

  @Override
  public HclLocation location() {
    return peek(0).start();
  }

  @Override
  public HclToken peek(int offset) throws HclProcessingException {
    while (tokens.size() < offset + 1) {
      tokens.push(lexer.nextToken());
    }

    return tokens.get(offset);
  }

  @Override
  public HclToken eat(HclTokenType type, HclTokenType... types) throws HclProcessingException {
    var token = peek(0);

    if (token.type() == type) {
      return token;
    }

    for (var anotherType : types) {
      if (token.type() == anotherType) {
        return token;
      }
    }

    throw new HclUnexpectedTokenException(
        token,
        EnumSet.of(type, types),
        lexer.charSource().name(),
        "Unexpected token in input"
    );
  }
}
