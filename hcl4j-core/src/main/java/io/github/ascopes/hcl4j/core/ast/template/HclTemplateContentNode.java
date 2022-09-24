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

import io.github.ascopes.hcl4j.core.inputs.HclLocatable;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;
import java.util.List;

/**
 * A template.
 *
 * @param items the components making up the template.
 * @param start the start location.
 * @param end   the end location.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record HclTemplateContentNode(
    List<? extends HclTemplateItemNode> items,
    @Override HclLocation start,
    @Override HclLocation end
) implements HclLocatable {
}
