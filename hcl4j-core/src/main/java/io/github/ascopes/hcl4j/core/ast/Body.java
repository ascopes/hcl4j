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

package io.github.ascopes.hcl4j.core.ast;

import io.github.ascopes.hcl4j.core.inputs.Location;
import java.util.List;

/**
 * Node that holds a body.
 *
 * @param bodyItems the items in the body.
 * @param start     the start location.
 * @param end       the end location.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record Body(
    List<? extends BodyItem> bodyItems,
    @Override Location start,
    @Override Location end
) implements Node {
}
