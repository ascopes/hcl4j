/*
 * Copyright (C) 2022 - 2022 Ashley Scopes
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

import io.github.ascopes.hcl4j.core.ast.body.HclBodyNode;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.inputs.HclCharSource;
import io.github.ascopes.hcl4j.core.lexer.HclDefaultLexer;
import io.github.ascopes.hcl4j.core.lexer.strategy.HclConfigLexerStrategy;

/**
 * Default parser for HCL config files.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class HclConfigFileParser extends HclDefaultParserBase<HclBodyNode> {

  /**
   * Initialize the parser.
   *
   * @param charSource the character source to use for input.
   */
  public HclConfigFileParser(HclCharSource charSource) {
    super(initializeTokenStream(charSource));
  }

  @Override
  public HclBodyNode parseFile() throws HclProcessingException {
    return body();
  }

  private static HclTokenStream initializeTokenStream(HclCharSource charSource) {
    var lexer = new HclDefaultLexer(charSource);
    var defaultMode = new HclConfigLexerStrategy(lexer);
    lexer.pushStrategy(defaultMode);
    return new HclDefaultTokenStream(lexer);
  }
}
