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
import io.github.ascopes.hcl4j.core.lexer.ConfigLexerStrategy;
import io.github.ascopes.hcl4j.core.nodes.BodyItem;
import java.io.IOException;
import java.util.List;

/**
 * Parser for HCL files.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class ConfigFileParser extends CommonParser<List<? extends BodyItem>> {

  /**
   * Initialize the parser.
   *
   * @param charSource the character source to use.
   */
  public ConfigFileParser(CharSource charSource) {
    super(charSource);
    context.pushStrategy(new ConfigLexerStrategy(context));
  }

  @Override
  public List<? extends BodyItem> root() throws IOException {
    throw new UnsupportedOperationException();
  }
}
