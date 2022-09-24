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
package io.github.ascopes.hcl4j.core.ast;

import io.github.ascopes.hcl4j.core.ast.body.HclBodyItemNode;
import io.github.ascopes.hcl4j.core.ast.collect.HclObjectKeyNode;
import io.github.ascopes.hcl4j.core.ast.expr.HclExpressionNode;
import io.github.ascopes.hcl4j.core.ast.func.HclParameterNode;
import io.github.ascopes.hcl4j.core.ast.id.HclIdentifierLikeNode;
import io.github.ascopes.hcl4j.core.ast.template.HclTemplateItemNode;
import io.github.ascopes.hcl4j.core.ast.template.HclTemplatePartNode;
import io.github.ascopes.hcl4j.core.inputs.HclLocatable;

/**
 * Algebraic base for all HCL AST node types.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclNode extends HclLocatable permits
    HclBodyItemNode,
    HclObjectKeyNode,
    HclExpressionNode,
    HclParameterNode,
    HclIdentifierLikeNode,
    HclTemplateItemNode,
    HclTemplatePartNode {
}
