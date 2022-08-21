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

import io.github.ascopes.hcl4j.core.tokens.Token;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class StructuralLexer implements LexerModeControl {

  private final Deque<LexerMode> modeStack;

  @SuppressWarnings("ThisEscapedInObjectConstruction")
  public StructuralLexer(CharSource source) {
    modeStack = new LinkedList<>();
    modeStack.push(new DefaultLexerMode(source, this));
  }

  public String getMode() {
    return Objects.toString(modeStack.peekFirst(), "none");
  }

  @Override
  public void pushMode(LexerMode mode) {
    modeStack.push(mode);
  }

  @Override
  public void popMode() throws NoSuchElementException {
    modeStack.pop();
  }

  public Token nextToken() throws IOException {
    return modeStack.getFirst().nextToken();
  }
}
