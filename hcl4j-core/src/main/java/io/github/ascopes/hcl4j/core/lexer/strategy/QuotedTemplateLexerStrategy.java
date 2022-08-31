package io.github.ascopes.hcl4j.core.lexer.strategy;

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

import io.github.ascopes.hcl4j.core.annotations.CheckReturnValue;
import io.github.ascopes.hcl4j.core.lexer.utils.LexerContext;
import io.github.ascopes.hcl4j.core.lexer.utils.RawTokenBuilder;
import io.github.ascopes.hcl4j.core.tokens.LexerError;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import io.github.ascopes.hcl4j.core.tokens.impl.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.impl.SimpleToken;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Lexer strategy to handle quoted templates.
 * <p>This class is <strong>not</strong> thread-safe.
 *
 * <p>This assumes that the heredoc header has already been consumed and the first character will
 * either be the content or the end of the heredoc. Specifically a lexer should have already
 * consumed the header as defined by {@link HeredocHeaderLexerStrategy}.
 *
 * <p>This lexer allows for a small subset of token possibilities, along with some additional
 * behaviours.
 *
 * <ul>
 *   <li>An end-of-file marker will pop the current mode and emit the end of file token.</li>
 *   <li>A new line will be emitted (although it should be considered an error by the parser).</li>
 *   <li>A closing quote will pop the current mode and emit the closing quote token.</li>
 *   <li>An interpolation opening with tilde "<code>$&#123;~</code>" will emit a
 *      {@link TokenType#LEFT_INTERPOLATION_TRIM} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>An interpolation opening without a tilde "<code>$&#123;</code>" will emit a
 *      {@link TokenType#LEFT_INTERPOLATION} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>A directive opening with tilde "<code>%&#123;~</code>" will emit a
 *      {@link TokenType#LEFT_DIRECTIVE_TRIM} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>A directive opening without a tilde "<code>%&#123;</code>" will emit a
 *      {@link TokenType#LEFT_DIRECTIVE} token, and a new {@link ConfigLexerStrategy}
 *      will be pushed onto the lexer strategy stack.</li>
 *   <li>Anything else will be collected into a buffer until one of the above cases occurs.
 *      The text will then be emitted as {@link TokenType#RAW_TEXT} as long as at least one
 *      character occurred before the next token of a differing type appeared.
 *      This is with two additional caveats:
 *      <ul>
 *        <li>The literal string {@code &bsol;"} will translate to a literal {@code "}
 *           character.</li>
 *        <li>The literal string {@code &bsol;&bsol;} will translate to a literal {@code &bsol;}
 *           character.</li>
 *        <li>The literal string {@code &bsol;r} will translate to a literal carriage return
 *           character.</li>
 *        <li>The literal string {@code &bsol;n} will translate to a literal line feed
 *           character.</li>
 *        <li>The literal string {@code &bsol;t} will translate to a literal tab character.</li>
 *        <li>The literal string {@code &bsol;uXXXX} where {@code X} is a hexadecimal digit will
 *           translate to a literal character from the UTF-8 basic multilingual plane (BMP) at the
 *           numeric codepoint.</li>
 *        <li>The literal string {@code &bsol;UXXXXXXXX} where {@code X} is a hexadecimal digit will
 *           translate to a literal character from the UTF-8 supplementary plane at the
 *           numeric codepoint.</li>
 *        <li>A left-interpolation marker that is preceded by a dollar "<code>$$&#123;</code>"
 *           will be treated as an escape for a plain-text "<code>$&#123;</code>".</li>
 *        <li>A left-directive marker that is preceded by a percent "<code>%%&#123;</code>"
 *           will be treated as an escape for a plain-text "<code>%&#123;</code>".</li>
 *      </ul>
 *   </li>
 * </ul>
 *
 * @author Ashley Scopes
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public final class QuotedTemplateLexerStrategy extends CommonLexerStrategy {

  private final Queue<Token> errors;

  /**
   * Initialize the strategy.
   *
   * @param context the context to use.
   */
  public QuotedTemplateLexerStrategy(LexerContext context) {
    super(context);

    // We can emit encoding errors for bad escape sequences. Do this using a stack to
    // keep things simple.
    errors = new LinkedList<>();
  }

  @CheckReturnValue
  @Override
  public Token nextToken() throws IOException {
    if (!errors.isEmpty()) {
      return errors.remove();
    }

    if (context.charSource().startsWith("${")) {
      // Next expression is an interpolation.
      context.pushStrategy(new TemplateExpressionLexerStrategy(context));
      return context.charSource().peek(3) == '~'
          ? newToken(TokenType.LEFT_INTERPOLATION_TRIM, 3)
          : newToken(TokenType.LEFT_INTERPOLATION, 2);
    }

    if (context.charSource().startsWith("%{")) {
      // Next expression is a directive.
      context.pushStrategy(new TemplateExpressionLexerStrategy(context));
      return context.charSource().peek(3) == '~'
          ? newToken(TokenType.LEFT_DIRECTIVE_TRIM, 3)
          : newToken(TokenType.LEFT_DIRECTIVE, 2);
    }

    return switch (context.charSource().peek(0)) {
      case '"' -> {
        context.popStrategy();
        yield newToken(TokenType.QUOTE, 1);
      }
      case EOF -> {
        context.popStrategy();
        yield consumeEndOfFile();
      }
      // Technically invalid, but we allow it here to handle as an error later.
      case '\r', '\n' -> consumeNewLine();
      default -> consumeSomeText();
    };
  }

  private Token consumeSomeText() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder()
        .append(context.charSource().read());

    loop:
    while (true) {
      switch (context.charSource().peek(0)) {
        case EOF, '"' -> {
          break loop;
        }

        case '\\' -> consumeEscape(buff);

        case '$' -> {
          if (context.charSource().startsWith("${")) {
            break loop;
          }

          if (context.charSource().startsWith("$${")) {
            buff.append("${");
            context.charSource().advance(3);
            continue;
          }

          buff.append(context.charSource().read());
        }
        case '%' -> {
          if (context.charSource().startsWith("%{")) {
            break loop;
          }

          if (context.charSource().startsWith("%%{")) {
            buff.append("%{");
            context.charSource().advance(3);
            continue;
          }

          buff.append(context.charSource().read());
        }

        default -> buff.append(context.charSource().read());
      }
    }

    return new SimpleToken(TokenType.RAW_TEXT, buff.raw(), location);
  }

  private void consumeEscape(RawTokenBuilder buff) throws IOException {
    switch (context.charSource().peek(1)) {
      case 'n' -> {
        buff.append('\n');
        context.charSource().advance(2);
      }
      case 'r' -> {
        buff.append('\r');
        context.charSource().advance(2);
      }
      case 't' -> {
        buff.append('\t');
        context.charSource().advance(2);
      }
      case '\\' -> {
        buff.append('\\');
        context.charSource().advance(2);
      }
      case '"' -> {
        buff.append('"');
        context.charSource().advance(2);
      }
      case 'u' -> consumeBasicMultilingualPlaneEscape(buff);
      case 'U' -> consumeSupplementaryEscape(buff);
      default -> errors.add(newError(LexerError.MALFORMED_ESCAPE_SEQUENCE, 2));
    }
  }

  private void consumeBasicMultilingualPlaneEscape(RawTokenBuilder buff) throws IOException {
    var location = context.charSource().location();
    var digits = new RawTokenBuilder();

    for (var i = 2; i < 6; ++i) {
      var nextChar = context.charSource().peek(i);

      if (nextChar == EOF || !isHexadecimal(nextChar)) {
        errors.add(new ErrorToken(
            LexerError.MALFORMED_ESCAPE_SEQUENCE,
            "\\u" + digits.raw(),
            location
        ));
        return;
      }

      digits.append(nextChar);
    }

    // Skip over the escape
    context.charSource().advance(6);

    var bmp = Integer.parseInt(digits.raw().toString(), 16);

    buff.append(bmp);
  }

  private void consumeSupplementaryEscape(RawTokenBuilder buff) throws IOException {
    var location = context.charSource().location();
    var digits = new RawTokenBuilder();

    for (var i = 2; i < 10; ++i) {
      var nextChar = context.charSource().peek(i);

      if (nextChar == EOF || !isHexadecimal(nextChar)) {
        errors.add(new ErrorToken(
            LexerError.MALFORMED_ESCAPE_SEQUENCE,
            "\\U" + digits.raw(),
            location
        ));
        return;
      }

      digits.append(nextChar);
    }

    // Skip over the escape
    context.charSource().advance(10);

    var higher = Integer.parseInt(digits.raw().subSequence(0, 4).toString(), 16);
    var lower = Integer.parseInt(digits.raw().subSequence(4, 8).toString(), 16);

    buff.append(higher)
        .append(lower);
  }
}
