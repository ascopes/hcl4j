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
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Distributed lexer state holder.
 *
 * <p>This is passed between lexer modes to represent the global lexer state. Lexer modes can
 * be pushed and popped to change the source of the next token.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public final class LexerContext implements AutoCloseable {

  private final CharSource charSource;
  private final Deque<LexerStrategy> strategyStack;

  /**
   * Initialize the lexer context.
   *
   * @param charSource the character source to use.
   */
  public LexerContext(CharSource charSource) {
    this.charSource = charSource;
    strategyStack = new LinkedList<>();
  }

  /**
   * Close the character source.
   *
   * @throws IOException if an {@link IOException} occurs during closure.
   */
  @Override
  public void close() throws IOException {
    charSource.close();
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
   * Get the number of strategies on the stack.
   *
   * @return the number of strategies on the stack.
   */
  public int stackDepth() {
    return strategyStack.size();
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
   * Retrieve the active lexer strategy.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  @CheckReturnValue
  public LexerStrategy activeStrategy() throws NoSuchElementException {
    var active = strategyStack.peek();

    if (active == null) {
      throw expectAtLeastOne();
    }

    return active;
  }

  private NoSuchElementException expectAtLeastOne() {
    return new NoSuchElementException("LexerContext mode stack is empty");
  }
}
