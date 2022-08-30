package io.github.ascopes.hcl4j.core.tokens;

/**
 * Enum of acceptable types of token that a lexer mode can emit.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public enum TokenType {
  // LexerContext control symbols.
  END_OF_FILE,
  ERROR,

  // Whitespace tokens
  NEW_LINE, WHITESPACE,

  // Comments
  INLINE_COMMENT, LINE_COMMENT,

  // Arithmetic operators
  PLUS, MINUS, STAR, DIVIDE, MODULO,

  // Comparison operators
  EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,

  // Logic operators
  AND, OR, NOT,

  // Block symbols
  LEFT_BRACE, RIGHT_BRACE, LEFT_INTERPOLATION, LEFT_DIRECTIVE,
  QUOTE,
  HEREDOC_ANCHOR,
  HEREDOC_INDENT_MARKER,

  // Indexing and attribute access symbols
  DOT, ELLIPSIS,
  LEFT_SQUARE, RIGHT_SQUARE,

  // Assignments
  ASSIGN, FAT_ARROW,

  // Ternary operator
  QUESTION_MARK, COLON,

  // Other symbols
  LEFT_PAREN, RIGHT_PAREN,
  COMMA,
  TILDE,

  // Literal values
  IDENTIFIER,
  NUMBER,

  // Textual content (within template objects).
  TEXT,
}
