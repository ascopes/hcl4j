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

package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.inputs.HclCharSource;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Distributed lexer state holder and concrete implementation to provide to a parser.
 *
 * <p>This is passed between lexer modes to represent the global lexer state. Lexer modes
 * can be pushed and popped to change the source of the next token.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclDefaultLexer implements HclLexer {

  private final HclCharSource charSource;
  private final Deque<HclLexerStrategy> strategyStack;

  /**
   * Initialize the lexer context.
   *
   * @param charSource the character source to use.
   */
  public HclDefaultLexer(HclCharSource charSource) {
    this.charSource = charSource;
    strategyStack = new LinkedList<>();
  }

  @Override
  public HclCharSource charSource() {
    return charSource;
  }

  @Override
  public void pushStrategy(HclLexerStrategy mode) {
    strategyStack.push(mode);
  }

  @Override
  public void popStrategy() throws NoSuchElementException {
    if (strategyStack.pop() == null) {
      throw expectAtLeastOne();
    }
  }

  @Override
  public HclToken nextToken() throws HclProcessingException {
    var strategy = strategyStack.peek();

    if (strategy == null) {
      throw expectAtLeastOne();
    }

    return strategy.nextToken();
  }

  private NoSuchElementException expectAtLeastOne() {
    return new NoSuchElementException("Lexer mode stack is empty");
  }
}
