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
import io.github.ascopes.hcl4j.core.intern.Nullable;

/**
 * An {@code if} directive.
 *
 * @param ifPart    the {@code if} clause.
 * @param elsePart  the {@code else} clause, if present.
 * @param endIfPart the {@code endif} clause.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTemplateIfNode(
    HclTemplateIfPartNode ifPart,
    @Nullable HclTemplateElsePartNode elsePart,
    HclTemplateEndPartNode endIfPart
) implements HclTemplateItemNode {

  @Override
  public HclLocation start() {
    return ifPart.start();
  }

  @Override
  public HclLocation end() {
    return endIfPart.end();
  }
}
