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
package io.github.ascopes.hcl4j.core.ast.expr;

import io.github.ascopes.hcl4j.core.ast.collect.HclCollectionValueNode;
import io.github.ascopes.hcl4j.core.ast.func.HclFunctionCallNode;
import io.github.ascopes.hcl4j.core.ast.getattr.HclGetAttrNode;
import io.github.ascopes.hcl4j.core.ast.id.HclVariableExprNode;
import io.github.ascopes.hcl4j.core.ast.index.HclIndexNode;
import io.github.ascopes.hcl4j.core.ast.index.HclLegacyIndexNode;
import io.github.ascopes.hcl4j.core.ast.iter.HclForExprNode;
import io.github.ascopes.hcl4j.core.ast.literal.HclLiteralValueNode;
import io.github.ascopes.hcl4j.core.ast.splat.HclSplatNode;
import io.github.ascopes.hcl4j.core.ast.template.HclTemplateExprNode;

/**
 * Valid types of expression terms.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclExprTermNode extends HclExpressionNode permits
    HclCollectionValueNode,
    HclWrappedExpressionNode,
    HclFunctionCallNode,
    HclGetAttrNode,
    HclVariableExprNode,
    HclIndexNode,
    HclLegacyIndexNode,
    HclForExprNode,
    HclLiteralValueNode,
    HclSplatNode,
    HclTemplateExprNode {}
