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

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.inputs.CharSource;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.io.IOException;
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
public final class Lexer {

  private final CharSource charSource;
  private final Deque<LexerStrategy> strategyStack;

  /**
   * Initialize the lexer context.
   *
   * @param charSource the character source to use.
   */
  public Lexer(CharSource charSource) {
    this.charSource = charSource;
    strategyStack = new LinkedList<>();
  }

  /**
   * Get the character source for the lexer.
   *
   * @return the character source.
   */
  @CheckReturnValue
  public CharSource charSource() {
    return charSource;
  }

  /**
   * Push a new strategy onto the lexer strategy stack.
   *
   * @param mode the lexer strategy to push.
   */
  public void pushStrategy(LexerStrategy mode) {
    strategyStack.push(mode);
  }

  /**
   * Pop a strategy from the lexer strategy stack.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  public void popStrategy() throws NoSuchElementException {
    if (strategyStack.pop() == null) {
      throw expectAtLeastOne();
    }
  }

  /**
   * Retrieve the next token.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  @CheckReturnValue
  public Token nextToken() throws IOException {
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