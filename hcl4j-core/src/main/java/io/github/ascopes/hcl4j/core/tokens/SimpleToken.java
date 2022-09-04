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

package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.inputs.Location;

/**
 * Standard representation of token that represents part of an HCL file.
 *
 * @param type  the token type.
 * @param raw   the token content.
 * @param start the start location.
 * @param end   the end location.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record SimpleToken(
    @Override TokenType type,
    @Override CharSequence raw,
    @Override Location start,
    @Override Location end
) implements Token {

}
