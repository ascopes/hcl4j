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

package io.github.ascopes.hcl4j.core.test.inputs;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ascopes.hcl4j.core.inputs.BufferedUtf8BomReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BufferedUtf8BomReader}.
 *
 * @author Ashley Scopes
 */
@DisplayName("BufferedUtf8BomReader tests")
class BufferedUtf8BomReaderTest {

  @DisplayName("The reader does not discard anything if the BOM is not present at the start")
  @Test
  void doesNotDiscardInitialCharactersIfNoBomIsPresent() throws IOException {
    // Given
    var data = new byte[]{'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd'};
    var input = new ByteArrayInputStream(data);

    // When
    var reader = new BufferedUtf8BomReader(input);

    // Then
    assertThat(reader.readLine()).isEqualTo("helloworld");
  }

  @DisplayName("The reader does not discard the BOM if it is not at the start")
  @Test
  void doesNotDiscardBomIfNotAtStart() throws IOException {
    // Given
    var data = new byte[]{'h', 'e', 'l', (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'l', 'o'};
    var input = new ByteArrayInputStream(data);

    // When
    var reader = new BufferedUtf8BomReader(input);

    // Then
    assertThat(reader.readLine()).isEqualTo("hel\uFEFFlo");
  }

  @DisplayName("The reader discards the BOM if the BOM is present at the start")
  @Test
  void discardInitialCharactersIfBomIsPresentAtStart() throws IOException {
    // Given
    var data = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'h', 'e', 'l', 'l', 'o'};
    var input = new ByteArrayInputStream(data);

    // When
    var reader = new BufferedUtf8BomReader(input);

    // Then
    assertThat(reader.readLine()).isEqualTo("hello");
  }
}
