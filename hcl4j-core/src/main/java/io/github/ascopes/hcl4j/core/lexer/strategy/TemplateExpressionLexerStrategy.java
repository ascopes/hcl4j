package io.github.ascopes.hcl4j.core.lexer.strategy;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.lexer.utils.LexerContext;
import io.github.ascopes.hcl4j.core.tokens.LexerError;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Strategy for tokenizing content that occurs within interpolation blocks or directive blocks.
 *
 * <p>This delegates to {@link ConfigLexerStrategy} internally, but provides additional behaviours
 * that would not be handled elsewhere, such as the "<code>~}</code>" token.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public final class TemplateExpressionLexerStrategy extends CommonLexerStrategy {

  private final ConfigLexerStrategy configLexerStrategy;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public TemplateExpressionLexerStrategy(LexerContext context) {
    super(context);
    configLexerStrategy = new ConfigLexerStrategy(context);
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    if (context.charSource().peek(0) == '~') {
      return consumeTilde();
    } else {
      return configLexerStrategy.nextToken();
    }
  }

  @CheckReturnValue
  private Token consumeTilde() throws IOException {
    if (context.charSource().peek(1) == '}') {
      context.popStrategy();
      return newToken(TokenType.RIGHT_BRACE_TRIM, 2);
    }

    return newError(LexerError.UNKNOWN_OPERATOR, 1);
  }
}
