package io.github.ascopes.hcl4j.core.lexer.strategy;

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

import io.github.ascopes.hcl4j.core.lexer.utils.LexerContext;
import io.github.ascopes.hcl4j.core.lexer.utils.RawTokenBuilder;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import io.github.ascopes.hcl4j.core.tokens.impl.SimpleToken;
import java.io.IOException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Lexer strategy for parsing an inline comment.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This expects the opening delimiter to have already been consumed by the lexer strategy that
 * called this one.
 *
 * <p>This implementation handles three states:
 *
 * <ul>
 *   <li>Next character is the end-of-file marker - the lexer strategy is popped and a
 *      {@link TokenType#END_OF_FILE}</li>
 *   <li>The next two characters are "{@code *}" and "{@code /}" - the lexer will emit a
 *      {@link TokenType#INLINE_COMMENT_END} token and pop the current strategy.
 *   <li>Any other characters are consumed until one of the above cases occurs, and will be
 *      emitted in a {@link TokenType#COMMENT_CONTENT} token as long as at least one character
 *      has been read.</li>
 * </ul>
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public final class InlineCommentLexerStrategy extends CommonLexerStrategy {

  /**
   * Initialize this strategy.
   *
   * @param context the lexer context to use.
   */
  public InlineCommentLexerStrategy(LexerContext context) {
    super(context);
  }

  @Override
  public Token nextToken() throws IOException {
    if (context.charSource().startsWith("*/")) {
      context.popStrategy();
      return newToken(TokenType.INLINE_COMMENT_END, 2);
    }

    var nextChar = context.charSource().peek(0);

    if (nextChar == EOF) {
      context.popStrategy();
      return consumeEndOfFile();
    }

    var location = context.charSource().location();
    var buff = new RawTokenBuilder()
        .append(context.charSource().read());

    while (true) {
      nextChar = context.charSource().peek(0);

      if (context.charSource().startsWith("*/") || nextChar == EOF) {
        break;
      }

      buff.append(nextChar);
      context.charSource().advance(1);
    }

    return new SimpleToken(TokenType.COMMENT_CONTENT, buff.raw(), location);
  }
}
