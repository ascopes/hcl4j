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

import io.github.ascopes.hcl4j.core.lexer.Location;
import io.github.ascopes.hcl4j.core.utils.HclTextUtils;
import io.github.ascopes.hcl4j.core.utils.ToStringBuilder;

public final class OperatorToken extends AbstractToken {

  private final Operator type;

  public OperatorToken(Location location, Operator type, String raw) {
    super(location, raw);
    this.type = type;
  }

  public OperatorToken(Location location, Operator type, int... chars) {
    this(location, type, HclTextUtils.join(chars));
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .add("location", getLocation())
        .add("type", type)
        .toString();
  }

  public Operator getType() {
    return type;
  }

  /**
   * Known operators in HCL.
   */
  public enum Operator {

    //////////////////////////
    // Arithmetic operators //
    //////////////////////////
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,

    ///////////////////////
    // Boolean operators //
    ///////////////////////
    AND,
    OR,
    NOT,

    //////////////////////////
    // Comparison operators //
    //////////////////////////
    EQUAL,
    NOT_EQUAL,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,

    //////////////
    // Brackets //
    //////////////
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_SQUARE,
    RIGHT_SQUARE,
    LEFT_BRACE,
    RIGHT_BRACE,

    /////////////////////////////
    // Various control symbols //
    /////////////////////////////
    ASSIGN,
    QUESTION_MARK,
    COLON,
    DOT,
    ELLIPSIS,
    COMMA,
    FAT_ARROW,

    /////////////////////////
    // Template characters //
    /////////////////////////
    TEMPLATE_DEDENT,
    INTERP_START,
    CONTROL_START,
  }
}
