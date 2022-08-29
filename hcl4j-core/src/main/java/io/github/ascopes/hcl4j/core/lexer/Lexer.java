package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.inputs.CharSource;
import io.github.ascopes.hcl4j.core.tokens.Token;
import java.io.IOException;

/**
 * Lexer implementation.
 *
 * <p>This will work by delegating to a {@link LexerMode} provided by the constructor. This lexer
 * mode can push additional lexer modes onto the internal stack to change the lexing strategy being
 * used. This enables parsing context-bound grammars easily without a mess of code all in a single
 * class.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class Lexer {

  private final LexerContext context;

  /**
   * Initialize the lexer.
   *
   * @param source                   the character source to use for input.
   * @param initialLexerModeProvider the provider of the initial lexer mode to use.
   */
  public Lexer(CharSource source, InitialLexerModeProvider initialLexerModeProvider) {
    context = new LexerContext(source);
    context.pushMode(initialLexerModeProvider.initialize(context));
  }

  /**
   * Get the next token in the file from the current active lexer mode.
   *
   * @return the next token.
   * @throws IOException if an {@link IOException} occurs internally while reading the input
   *                     source.
   */
  public Token nextToken() throws IOException {
    return context.activeMode().nextToken();
  }

  /**
   * Functional interface for the initializer of the first lexer mode to use when reading the input
   * source.
   */
  @FunctionalInterface
  public interface InitialLexerModeProvider {

    /**
     * Initialize a lexer mode from the given lexer context.
     *
     * @param context the lexer context to use.
     * @return the lexer mode to initialize.
     */
    LexerMode initialize(LexerContext context);
  }
}
