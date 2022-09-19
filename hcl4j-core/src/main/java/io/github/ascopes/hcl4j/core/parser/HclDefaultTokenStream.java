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
import io.github.ascopes.hcl4j.core.intern.Indexed;
import io.github.ascopes.hcl4j.core.lexer.HclLexer;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.util.EnumSet;
import java.util.LinkedList;

/**
 * A simple wrapper around a lexer that provides useful stream-oriented operations for parsers to
 * consume tokens with. This enables parsers to become {@code LL(k)} look-ahead, and will discard
 * whitespace automatically.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclDefaultTokenStream implements HclTokenStream {

  private final HclLexer lexer;
  private final LinkedList<HclToken> tokens;
  private EnumSet<HclTokenType> skipMask;

  /**
   * Initialize this stream.
   *
   * @param lexer the lexer to wrap.
   */
  public HclDefaultTokenStream(HclLexer lexer) {
    this.lexer = lexer;
    tokens = new LinkedList<>();
    skipMask = EnumSet.noneOf(HclTokenType.class);
  }

  @Override
  public String name() {
    return lexer.charSource().name();
  }

  @Override
  public void ignoreToken(HclTokenType tokenType) {
    if (tokenType == HclTokenType.EOF) {
      throw new IllegalArgumentException("Cannot ignore EOF");
    }

    skipMask.add(tokenType);
  }

  @Override
  public HclLocation location() {
    return peek(0).start();
  }

  @Override
  public HclToken peek(int offset) throws HclProcessingException {
    return retrieveToken(offset).object();
  }

  @Override
  public HclToken eat(HclTokenType type, HclTokenType... types) throws HclProcessingException {
    var indexedToken = retrieveToken(0);
    var index = indexedToken.index();
    var token = indexedToken.object();

    if (token.type() == type) {
      removeCachedTokensUntilIndex(index + 1);
      return token;
    }

    for (var anotherType : types) {
      if (token.type() == anotherType) {
        removeCachedTokensUntilIndex(index + 1);
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

  private Indexed<HclToken> retrieveToken(int offset) {
    var currentOffset = 0;
    var index = 0;

    for (HclToken next : tokens) {
      if (skipMask.contains(next.type())) {
        ++index;
        continue;
      }

      if (currentOffset == offset || next.type() == HclTokenType.EOF) {
        return new Indexed<>(index, next);
      }

      ++currentOffset;
      ++index;
    }

    while (true) {
      var next = lexer.nextToken();
      tokens.add(next);

      if (skipMask.contains(next.type())) {
        ++index;
        continue;
      }

      if (currentOffset == offset || next.type() == HclTokenType.EOF) {
        return new Indexed<>(index, next);
      }

      ++currentOffset;
      ++index;
    }
  }

  private void removeCachedTokensUntilIndex(int index) {
    var currentIndex = 0;

    while (!tokens.isEmpty() && currentIndex < index) {
      tokens.removeFirst();
      ++currentIndex;
    }
  }
}
