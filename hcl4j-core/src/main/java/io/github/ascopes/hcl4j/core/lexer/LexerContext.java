package io.github.ascopes.hcl4j.core.lexer;

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
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class LexerContext {

  private final CharSource charSource;
  private final Deque<LexerMode> modeStack;

  /**
   * Initialize the lexer context.
   *
   * @param charSource the character source to use.
   */
  public LexerContext(CharSource charSource) {
    this.charSource = charSource;
    modeStack = new LinkedList<>();
  }

  /**
   * Get the character source for the lexer.
   *
   * @return the character source.
   */
  public CharSource charSource() {
    return charSource;
  }

  /**
   * Push a new mode onto the lexer mode stack.
   *
   * @param mode the lexer mode to use.
   */
  public void pushMode(LexerMode mode) {
    modeStack.push(mode);
  }

  /**
   * Pop a mode from the lexer mode stack.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  public void popMode() throws NoSuchElementException {
    if (modeStack.pop() == null) {
      throw new NoSuchElementException("LexerContext mode stack is empty");
    }
  }

  /**
   * Retrieve the active lexer mode.
   *
   * @throws NoSuchElementException if the stack is empty.
   */
  public LexerMode activeMode() throws NoSuchElementException {
    var active = modeStack.peek();

    if (active == null) {
      throw new NoSuchElementException("LexerContext mode stack is empty");
    }

    return active;
  }
}
