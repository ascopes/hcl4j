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
package io.github.ascopes.hcl4j.core.ast.template;

import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import io.github.ascopes.hcl4j.core.tokens.HclToken;

/**
 * A literal piece of valueToken within a template.
 *
 * @param valueToken the raw content token.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTemplateLiteralNode(HclToken valueToken) implements HclTemplateItemNode {

  @Override
  public HclLocation start() {
    return valueToken.start();
  }

  @Override
  public HclLocation end() {
    return valueToken.end();
  }

  public CharSequence value() {
    return valueToken.content();
  }
}
