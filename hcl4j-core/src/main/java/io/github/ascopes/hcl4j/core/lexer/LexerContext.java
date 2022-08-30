package io.github.ascopes.hcl4j.core.lexer;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.inputs.CharSource;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Distributed lexer state holder.
 *
 * <p>This is passed between lexer modes to represent the global lexer state. Lexer modes can
 * be pushed and popped to change the source of the next token.
 *
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class LexerContext {

  private final CharSource charSource;
  private final Deque<LexerStrategy> strategyStack;

  /**
   * Initialize the lexer context.
   *
   * @param charSource the character source to use.
   */
  public LexerContext(CharSource charSource) {
    this.charSource = charSource;
    strategyStack = new LinkedList<>();
  }

  /**
   * Get the character source for the lexer.
   *
   * @return the character source.
   */
  @CheckReturnValue
  public CharSource charSource() {
    return charSource;
  }

  /**
   * Push a new mode onto the lexer mode stack.
   *
   * @param mode the lexer mode to use.
   */
  public void pushMode(LexerStrategy mode) {
    strategyStack.push(mode);
  }

  /**
   * Pop a mode from the lexer mode stack.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  public void popMode() throws NoSuchElementException {
    if (strategyStack.pop() == null) {
      throw expectAtLeastOne();
    }
  }

  /**
   * Retrieve the active lexer mode.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  @CheckReturnValue
  public LexerStrategy activeMode() throws NoSuchElementException {
    var active = strategyStack.peek();

    if (active == null) {
      throw expectAtLeastOne();
    }

    return active;
  }

  private NoSuchElementException expectAtLeastOne() {
    return new NoSuchElementException("LexerContext mode stack is empty");
  }
}
