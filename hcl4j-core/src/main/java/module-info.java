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

module io.github.ascopes.hcl4j.core {
  requires java.base;

  exports io.github.ascopes.hcl4j.core.ast;
  exports io.github.ascopes.hcl4j.core.inputs;
  exports io.github.ascopes.hcl4j.core.lexer;
  exports io.github.ascopes.hcl4j.core.lexer.strategy;
  exports io.github.ascopes.hcl4j.core.tokens;

  exports io.github.ascopes.hcl4j.core.intern to io.github.ascopes.hcl4j.core.test;
}
