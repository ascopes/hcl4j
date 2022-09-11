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
import io.github.ascopes.hcl4j.core.intern.Nullable;
import io.github.ascopes.hcl4j.core.lexer.HclLexer;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.util.EnumSet;

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

  @Nullable
  private HclToken nextToken;

  /**
   * Initialize this stream.
   *
   * @param lexer the lexer to wrap.
   */
  public HclSimpleTokenStream(HclLexer lexer) {
    this.lexer = lexer;
    nextToken = null;
  }

  @Override
  public HclLocation location() {
    return getOrReadCurrentToken().start();
  }

  @Override
  public HclTokenType type() throws HclProcessingException {
    return getOrReadCurrentToken().type();
  }

  @Override
  public HclToken eat(HclTokenType type, HclTokenType... types) throws HclProcessingException {
    var token = getOrReadCurrentToken();

    if (type == token.type()) {
      nextToken = lexer.nextToken();
      return token;
    }

    for (var anotherType : types) {
      if (anotherType == token.type()) {
        nextToken = lexer.nextToken();
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

  private HclToken getOrReadCurrentToken() throws HclProcessingException {
    return nextToken == null
        ? (nextToken = lexer.nextToken())
        : nextToken;
  }
}
