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

/**
 * Lexer mode for handling interpolation sequences.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class InterpolationLexerMode implements LexerMode {

  private final CharSource source;
  private final LexerModeControl lexer;

  public InterpolationLexerMode(CharSource source, LexerModeControl lexer) {
    this.source = source;
    this.lexer = lexer;
  }

  @Override
  public Token nextToken() throws IOException {
    throw new UnsupportedOperationException();
  }
}
