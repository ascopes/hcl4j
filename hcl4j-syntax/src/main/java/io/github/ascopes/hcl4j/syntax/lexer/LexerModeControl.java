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

package io.github.ascopes.hcl4j.syntax.lexer;

import java.util.NoSuchElementException;

/**
 * Operations that a lexer should expose to internal lexer modes to control the lexer stack.
 *
 * <p>This allows lexer modes to be able to terminate themselves or start new nested modes when
 * certain syntatic patterns and other events arise.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public interface LexerModeControl {

  /**
   * Push a new mode onto the lexer mode stack.
   *
   * @param mode the mode to push.
   */
  void pushMode(LexerMode mode);

  /**
   * Pop the top-most mode from the lexer stack.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  void popMode() throws NoSuchElementException;
}
