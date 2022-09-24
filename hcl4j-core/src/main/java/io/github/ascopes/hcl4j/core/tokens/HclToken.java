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
package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.inputs.HclLocatable;
import io.github.ascopes.hcl4j.core.inputs.HclLocation;

/**
 * Trait representing the interface of a token emitted by the lexer.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public sealed interface HclToken
    extends HclLocatable
    permits HclEofToken, HclRawTextToken, HclDefaultToken {

  /**
   * Get the token type.
   *
   * @return the token type.
   */
  HclTokenType type();

  /**
   * The processed content of the token, if applicable.
   *
   * <p>Most cases of this method will return the raw content, but this is useful for handling
   * special cases like string contents where escape codes may have been processed.
   *
   * @return the content.
   */
  default CharSequence content() {
    return raw();
  }

  /**
   * Get the raw content of the token.
   *
   * @return the raw content.
   */
  CharSequence raw();

  /**
   * Get the location start.
   *
   * @return the location start of the token.
   */
  HclLocation start();

  /**
   * Get the location end.
   *
   * @return the location end of the token.
   */
  HclLocation end();

  /**
   * Determine if the raw content of this token equals the given string.
   *
   * @param string the string to compare to.
   * @return {@code true} if the string has the same raw content as this token, or {@code false}
   *     otherwise.
   */
  default boolean rawEquals(CharSequence string) {
    return charSequencesSame(raw(), string);
  }

  /**
   * Determine if the interpreted content of this token equals the given string.
   *
   * @param string the string to compare to.
   * @return {@code true} if the string has the same content as this token, or {@code false}
   *     otherwise.
   */
  default boolean contentEquals(CharSequence string) {
    return charSequencesSame(content(), string);
  }


  // Perform these checks manually, as there is no guarantee two different types of CharSequence
  // will be considered equal when tested.
  private static boolean charSequencesSame(CharSequence a, CharSequence b) {
    var len = a.length();

    if (len != b.length()) {
      return false;
    }

    for (var i = 0; i < len; ++i) {
      if (a.charAt(i) != b.charAt(i)) {
        return false;
      }
    }

    return true;
  }
}
