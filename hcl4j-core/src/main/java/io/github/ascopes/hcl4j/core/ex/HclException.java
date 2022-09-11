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

package io.github.ascopes.hcl4j.core.ex;

/**
 * Base for any exception raised by HCL4J libraries.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class HclException extends RuntimeException {

  /**
   * Initialise this exception.
   *
   * @param message the exception message.
   */
  protected HclException(String message) {
    super(message);
  }

  /**
   * Initialise this exception.
   *
   * @param message the exception message.
   * @param cause   the exception cause.
   */
  protected HclException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Produce a quoted representation of some raw input content to use in a message. This content
   * will be quoted and escaped to keep the message clear. Any characters outside the space
   * character, and readable ASCII characters will be represented using a UTF-8 escape code. The
   * horizontal tab, carriage return, line feed, null terminator, backslash, and double quote
   * characters will be represented using an ANSI-like escape code.
   *
   * @param content the content to produce a representation of.
   * @return the string representation.
   */
  protected static String safeRepr(CharSequence content) {
    var buff = new StringBuilder("\"");
    var len = content.length();

    for (var i = 0; i < len; ++i) {
      var next = content.charAt(i);

      switch (next) {
        case '\0' -> buff.append("\\0");
        case '\n' -> buff.append("\\n");
        case '\r' -> buff.append("\\r");
        case '\t' -> buff.append("\\t");
        case '\\' -> buff.append("\\\\");
        case '\"' -> buff.append("\\\"");
        default -> {
          if (next >= 0x20 && next < 0x7F || next >= 0x80 && next <= 0xFF) {
            buff.append(next);
          } else {
            buff.append(String.format("\\u%04x", (int) next));
          }
        }
      }
    }

    return buff.append("\"").toString();
  }
}
