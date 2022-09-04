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
import io.github.ascopes.hcl4j.core.inputs.Range;
import io.github.ascopes.hcl4j.core.inputs.RawContentBuffer;
import io.github.ascopes.hcl4j.core.tokens.SimpleToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;

/**
 * LexerContext strategy for parsing a line comment.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This expects the opening delimiter to have already been consumed by the lexer strategy that
 * called this one.
 *
 * <p>This implementation will consume characters until a new line is encountered, or the
 * end-of-file marker is reached. The content will be emitted within a
 * {@link TokenType#COMMENT_CONTENT} token if at least one character has been consumed.
 *
 * <p>The {@link TokenType#END_OF_FILE} or {@link TokenType#NEW_LINE} will be emitted before the
 * lexer strategy is popped.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class LineCommentLexerStrategy extends CommonLexerStrategy {

  /**
   * Initialize this strategy.
   *
   * @param context the context to use.
   */
  public LineCommentLexerStrategy(LexerContext context) {
    super(context);
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    var nextChar = context.charSource().peek(0);

    if (isNewLineStart(nextChar)) {
      context.popStrategy();
      return consumeNewLine();
    }

    if (nextChar == EOF) {
      context.popStrategy();
      return consumeEndOfFile();
    }

    var start = context.charSource().location();
    var buff = new RawContentBuffer()
        .append(context.charSource().read());

    while (true) {
      nextChar = context.charSource().peek(0);

      if (isNewLineStart(nextChar) || nextChar == EOF) {
        break;
      }

      buff.append(nextChar);
      context.charSource().advance(1);
    }

    var end = context.charSource().location();
    var range = new Range(start, end);

    return new SimpleToken(TokenType.COMMENT_CONTENT, buff.content(), range);
  }
}
