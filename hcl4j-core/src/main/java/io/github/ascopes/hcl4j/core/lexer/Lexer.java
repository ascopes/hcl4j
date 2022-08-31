package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.inputs.CharSource;
import io.github.ascopes.hcl4j.core.lexer.strategy.ConfigLexerStrategy;
import io.github.ascopes.hcl4j.core.lexer.strategy.LexerStrategy;
import io.github.ascopes.hcl4j.core.lexer.strategy.TemplateFileLexerStrategy;
import io.github.ascopes.hcl4j.core.lexer.utils.LexerContext;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.impl.EofToken;
import java.io.IOException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Lexer implementation.
 *
 * <p>This will work by delegating to a {@link LexerStrategy} provided by the constructor. This
 * lexer mode can push additional lexer modes onto the internal stack to change the lexing strategy
 * being used. This enables parsing context-bound grammars easily without a mess of code all in a
 * single class.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public final class Lexer implements AutoCloseable {

  private final LexerContext context;

  /**
   * Initialize the lexer.
   *
   * @param source                   the character source to use for input.
   * @param initialLexerModeProvider the provider of the initial lexer mode to use.
   */
  @API(since = "0.0.1", status = Status.INTERNAL)
  public Lexer(CharSource source, InitialLexerModeProvider initialLexerModeProvider) {
    context = new LexerContext(source);
    context.pushStrategy(initialLexerModeProvider.initialize(context));
  }

  /**
   * Close the character source.
   *
   * @throws IOException if an {@link IOException} occurs during closure.
   */
  @Override
  public void close() throws IOException {
    context.close();
  }

  /**
   * Get the next token in the file from the current active lexer mode.
   *
   * @return the next token.
   * @throws IOException if an {@link IOException} occurs internally while reading the input
   *                     source.
   */
  @CheckReturnValue
  public Token nextToken() throws IOException {
    if (context.stackDepth() == 0) {
      return new EofToken(context.charSource().location());
    }

    return context.activeStrategy().nextToken();
  }

  /**
   * Create a new lexer configured to tokenize an HCL config file.
   *
   * @param source the source file to read.
   * @return the lexer.
   */
  @CheckReturnValue
  public static Lexer forHclConfigFile(CharSource source) {
    return new Lexer(source, ConfigLexerStrategy::new);
  }

  /**
   * Create a new lexer configured to tokenize an external template file.
   *
   * @param source the source file to read.
   * @return the lexer.
   */
  @CheckReturnValue
  public static Lexer forTemplateFile(CharSource source) {
    return new Lexer(source, TemplateFileLexerStrategy::new);
  }

  /**
   * Functional interface for the initializer of the first lexer mode to use when reading the input
   * source.
   */
  @API(since = "0.0.1", status = Status.INTERNAL)
  @FunctionalInterface
  public interface InitialLexerModeProvider {

    /**
     * Initialize a lexer mode from the given lexer context.
     *
     * @param context the lexer context to use.
     * @return the lexer mode to initialize.
     */
    @CheckReturnValue
    LexerStrategy initialize(LexerContext context);
  }
}
