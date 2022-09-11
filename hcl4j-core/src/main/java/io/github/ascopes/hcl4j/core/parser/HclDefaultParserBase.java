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

package io.github.ascopes.hcl4j.core.parser;

/**
 * Class that implements the bulk of the HCL parser functionality without defining the root node
 * entrypoint.
 *
 * <p>Classes can use this implementation or define their own parsing mechanism.
 *
 * @param <T> the root node type.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class HclDefaultParserBase<T> implements HclParser<T> {

  protected final HclTokenStream tokenStream;

  /**
   * Initialize the parser base.
   *
   * @param tokenStream the token stream to use.
   */
  public HclDefaultParserBase(HclTokenStream tokenStream) {
    this.tokenStream = tokenStream;
  }
}
