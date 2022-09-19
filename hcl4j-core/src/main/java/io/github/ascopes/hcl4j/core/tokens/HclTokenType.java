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

import io.github.ascopes.hcl4j.core.intern.Nullable;

/**
 * Enum of acceptable types of token that a lexer mode can emit.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public enum HclTokenType {
  EOF("end-of-file"),
  NEW_LINE("new line"),
  WHITESPACE("whitespace"),

  INLINE_COMMENT_START("start of inline comment", "/*"),
  INLINE_COMMENT_END("end of inline comment", "*/"),
  LINE_COMMENT_SLASH_START("start of line comment", "//"),
  LINE_COMMENT_HASH_START("start of line comment", "#"),
  COMMENT_CONTENT("comment contents"),

  PLUS("plus sign", "+"),
  MINUS("minus sign", "-"),
  STAR("multiply/splat", "*"),
  DIVIDE("divide", "/"),
  MODULO("modulo", "%"),
  EQUAL("equality operator", "=="),
  NOT_EQUAL("inequality operator", "!="),
  LESS("less-than operator", "<"),
  LESS_EQUAL("less-than-or-equal-to operator", "<="),
  GREATER("greater-than operator", ">"),
  GREATER_EQUAL("greater-than-or-equal-to operator", ">="),
  AND("logical AND operator", "&&"),
  OR("logical OR operator", "||"),
  NOT("logical NOT operator", "!"),
  LEFT_BRACE("start of block", "{"),
  RIGHT_BRACE("end of block or sequence", "}"),
  TRIM("trim-sequence marker", "~"),
  LEFT_INTERPOLATION("start of template interpolation sequence", "${"),
  LEFT_DIRECTIVE("start of template directive sequence", "%{"),
  OPENING_QUOTE("opening quotation mark", "\""),
  CLOSING_QUOTE("closing quotation mark", "\""),
  HEREDOC_ANCHOR("start of a heredoc template", "<<"),
  HEREDOC_INDENT_MARKER("heredoc indentation marker", "-"),
  DOT("dot operator", "."),
  ELLIPSIS("ellipsis operator", "..."),
  LEFT_SQUARE("left square bracket", "["),
  RIGHT_SQUARE("right square bracket", "]"),
  ASSIGN("assignment operator", "="),
  FAT_ARROW("mapping operator", "=>"),
  QUESTION_MARK("ternary operator question-mark", "?"),
  COLON("colon operator", ":"),
  LEFT_PAREN("left parenthesis", "("),
  RIGHT_PAREN("right parenthesis", ")"),
  COMMA("commaToken operator", ","),

  IDENTIFIER("identifier or keyword"),
  INTEGER("integer literal"),
  REAL("real literal"),

  RAW_TEXT("raw text");

  private final String displayName;

  @Nullable
  private final String symbol;

  HclTokenType(String displayName) {
    this(displayName, null);
  }

  HclTokenType(String displayName, @Nullable String symbol) {
    this.displayName = displayName;
    this.symbol = symbol;
  }

  public String displayName() {
    return displayName;
  }

  @Nullable
  public String symbol() {
    return symbol;
  }
}
