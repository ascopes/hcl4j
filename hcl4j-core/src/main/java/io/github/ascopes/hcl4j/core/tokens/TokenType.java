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

/**
 * Enum of acceptable types of token that a lexer mode can emit.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public enum TokenType {
  END_OF_FILE("end of file"),
  WTF("error"),
  NEW_LINE("new line"),
  WHITESPACE("whitespace"),

  INLINE_COMMENT_START("/*"),
  INLINE_COMMENT_END("*/"),
  LINE_COMMENT_SLASH_START("//"),
  LINE_COMMENT_HASH_START("#"),
  COMMENT_CONTENT("comment content"),

  PLUS("+"),
  MINUS("-"),
  STAR("*"),
  DIVIDE("/"),
  MODULO("%"),
  EQUAL("="),
  NOT_EQUAL("!="),
  LESS("<"),
  LESS_EQUAL("<="),
  GREATER(">"),
  GREATER_EQUAL(">="),
  AND("&&"),
  OR("||"),
  NOT("!"),
  LEFT_BRACE("{"),
  RIGHT_BRACE("}"),
  RIGHT_BRACE_TRIM("~}"),
  LEFT_INTERPOLATION("${"),
  LEFT_INTERPOLATION_TRIM("${~"),
  LEFT_DIRECTIVE("%{"),
  LEFT_DIRECTIVE_TRIM("%{~"),
  QUOTE("\""),
  HEREDOC_ANCHOR("<<"),
  HEREDOC_INDENT_MARKER("-"),
  DOT("."),
  ELLIPSIS("..."),
  LEFT_SQUARE("["),
  RIGHT_SQUARE("]"),
  ASSIGN("="),
  FAT_ARROW("=>"),
  QUESTION_MARK("?"),
  COLON(":"),
  LEFT_PAREN("("),
  RIGHT_PAREN(")"),
  COMMA(","),

  IDENTIFIER("identifier"),
  INTEGER("integer"),
  REAL("real"),

  RAW_TEXT("valueToken");

  private final String displayName;

  TokenType(String displayName) {
    this.displayName = displayName;
  }

  public String displayName() {
    return displayName;
  }
}
