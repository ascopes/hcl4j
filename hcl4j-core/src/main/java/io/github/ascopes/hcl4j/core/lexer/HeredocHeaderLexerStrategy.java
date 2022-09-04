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

package io.github.ascopes.hcl4j.core.lexer;

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.annotations.Nullable;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;

/**
 * LexerContext strategy for tokenizing a heredoc header before initializing the actual heredoc
 * parser.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This is kept as a separate lexer as heredoc headers expect a specific set of tokens, and
 * anything deviating from this is considered to be garbage.
 *
 * <p>This lexer allows for a small subset of token possibilities, along with some additional
 * behaviours.
 *
 * <ul>
 *   <li>An end-of-file marker will trigger an {@code END_OF_FILE} token and pop the current
 *      strategy.</li>
 *   <li>A hyphen "{@code -}" will emit a {@link TokenType#HEREDOC_INDENT_MARKER}.</li>
 *   <li>An {@code ID_START} character will emit an {@link TokenType#IDENTIFIER} and cache this
 *      value.</li>
 *   <li>A line feed {@code LF} or carriage return and line feed {@code CRLF} will emit a
 *      {@link TokenType#NEW_LINE} token. If there is an identifier already cached, this identifier
 *      will be passed to a new {@link HeredocLexerStrategy}. This header lexer strategy will be
 *      popped and the new strategy will be pushed. If no identifier was discovered, we are in an
 *      erroneous state so we cannot do anything else. In this case we will pop the current mode
 *      only. The parser consuming this stream of tokens should decide how to handle this situation.
 *   </li>
 * </ul>
 *
 * <p>Also note that order of tokens, as well as errors such as multiple identifiers, will not be
 * checked here. Parsers should check this sequence matches the correct definition of a heredoc
 * header. Specifically, the following pattern should be consumed (assuming the
 * {@link TokenType#HEREDOC_ANCHOR} was produced by the lexer strategy that initialized this
 * strategy prior to the first {@link #nextToken()} call).
 *
 * <pre><code>
 *   HEREDOC_HEADER : HEREDOC_ANCHOR , HEREDOC_INDENT_MARKER? , IDENTIFIER , NEW_LINE
 * </code></pre>
 *
 * @author Ashley Scopes
 */
public final class HeredocHeaderLexerStrategy extends CommonLexerStrategy {

  @Nullable
  private CharSequence identifier;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public HeredocHeaderLexerStrategy(LexerContext context) {
    super(context);
    identifier = null;
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    var next = context.charSource().peek(0);

    if (next == EOF) {
      context.popStrategy();
      return consumeEndOfFile();
    }

    if (next == '-') {
      return newToken(TokenType.HEREDOC_INDENT_MARKER, 1);
    }

    if (isIdStart(next)) {
      var id = consumeIdentifier();
      identifier = id.raw();
      return id;
    }

    if (isNewLineStart(next)) {
      var newLine = consumeNewLine();

      // After the newline, it is time to pop the current mode and push the new mode if an
      // identifier is set. If it isn't set, skip handling a heredoc and let the previous lexer
      // continue.
      context.popStrategy();

      if (identifier != null) {
        context.pushStrategy(new HeredocLexerStrategy(context, identifier));
        return newLine;
      }
    }

    return consumeUnrecognisedCharacter();
  }
}
