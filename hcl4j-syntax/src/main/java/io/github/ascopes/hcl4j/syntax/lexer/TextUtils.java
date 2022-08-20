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

package io.github.ascopes.hcl4j.syntax.lexer;

/**
 * Helper functions for common HCL character handling logic.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class TextUtils {

  private TextUtils() {
    throw new UnsupportedOperationException("static-only class");
  }

  /**
   * Determine if the given character is an end-of-file marker (EOF).
   *
   * @param c the character to check.
   * @return {@code true} if the character is the end-of-file marker, {@code false} otherwise.
   */
  public static boolean isEof(int c) {
    return c == CharSource.EOF;
  }

  /**
   * Determine if the given character is a numeric digit (ASCII 0-9).
   *
   * @param c the character to check.
   * @return {@code true} if the character is a numeric digit, {@code false} otherwise.
   */
  public static boolean isNumeric(int c) {
    return '0' <= c && c <= '9';
  }

  /**
   * Determine if the given character is a valid Unicode {@code ID_START} character (start of a
   * Unicode identifier).
   *
   * @param c the character to check.
   * @return {@code true} if the character is a valid {@code ID_START} character, {@code false}
   * otherwise.
   */
  public static boolean isIdStart(int c) {
    return Character.isUnicodeIdentifierStart(c);
  }

  /**
   * Determine if the given character is a valid Unicode {@code ID_CONTINUE} character (any valid
   * Unicode identifier character that comes after {@code ID_START} in a Unicode identifier that
   * is still part of the identifier itself). This also returns true if the character is an ASCII
   * hyphen '{@code -}', as this is valid in HCL as well.
   *
   * @param c the character to check.
   * @return {@code true} if the character is a valid {@code ID_CONTINUE} character or hyphen,
   * {@code false} otherwise.
   */
  public static boolean isIdContinue(int c) {
    return Character.isUnicodeIdentifierPart(c);
  }

  /**
   * Join one or more characters together into a string.
   *
   * @param chars the characters to join.
   * @return the string of characters.
   */
  public static CharSequence join(int... chars) {
    if (chars.length == 0 || isEof(chars[0])) {
      return "";
    }

    if (chars.length == 1) {
      return Character.toString(chars[0]);
    }

    var sb = new StringBuilder();
    for (var c : chars) {
      if (isEof(c)) {
        break;
      }

      sb.append((char) c);
    }

    return sb.toString();
  }

  /**
   * Escape the given string for use in diagnostic messages such as {@link Object#toString()}
   * implementations.
   *
   * @param text the text to read.
   * @return the escaped text.
   */
  public static String escapeTextForDiagnostics(CharSequence text) {
    var sb = new StringBuilder();
    var len = text.length();

    for (var i = 0; i < len; ++i) {
      var c = text.charAt(i);

      switch (c) {
        case '\r':
          sb.append("\\r");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\"':
          sb.append("\\\"");
          break;
        default:
          if (0x20 <= c && c <= 0x7E || 0x80 <= c && c <= 0xFF) {
            sb.append(c);
          } else {
            sb.append(String.format("\\u%04x", (int) c));
          }
          break;
      }
    }

    return sb.toString();
  }
}
