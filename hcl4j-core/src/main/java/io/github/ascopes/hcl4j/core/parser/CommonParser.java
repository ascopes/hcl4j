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

import io.github.ascopes.hcl4j.core.inputs.CharSource;
import io.github.ascopes.hcl4j.core.lexer.LexerContext;

/**
 * Base functionality for all parsers to use.
 *
 * @param <T> the root node type that the parser will emit.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class CommonParser<T> implements Parser<T> {

  protected final LexerContext context;

  /**
   * Initialize the parser.
   *
   * @param charSource the character source to use.
   */
  protected CommonParser(CharSource charSource) {
    context = new LexerContext(charSource);
  }
}