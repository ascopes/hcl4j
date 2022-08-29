package io.github.ascopes.hcl4j.core.lexer;

import static io.github.ascopes.hcl4j.core.inputs.CharSource.EOF;

import io.github.ascopes.hcl4j.core.tokens.ErrorToken;
import io.github.ascopes.hcl4j.core.tokens.LexerError;
import io.github.ascopes.hcl4j.core.tokens.SimpleToken;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Default lexer mode used to parse HCL expressions outside templates.
 */
@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class DefaultLexerMode implements LexerMode {

  private final LexerContext context;
  private final Queue<Token> lookAheadQueue;

  public DefaultLexerMode(LexerContext context) {
    this.context = context;
    lookAheadQueue = new LinkedList<>();
  }

  @Override
  public Token nextToken() throws IOException {
    if (!lookAheadQueue.isEmpty()) {
      return lookAheadQueue.remove();
    }

    // Skip whitespace.
    var nextChar = context.charSource().peek(0);
    while (nextChar == ' ' || nextChar == '\t') {
      context.charSource().advance(1);
      nextChar = context.charSource().peek(0);
    }

    // Handle end-of-files
    if (nextChar == EOF) {
      return consumeEndOfFile();
    }

    // Handle new lines
    if (nextChar == '\r' || nextChar == '\n') {
      return consumeNewLine();
    }

    // Handle regular tokens.
    if (isIdStart(nextChar)) {
      return consumeIdentifier();
    }

    if (isDigit(nextChar)) {
      return consumeNumber();
    }

    return switch (nextChar) {
      case '+' -> consumePlus();
      case '-' -> consumeMinus();
      case '*' -> consumeAsterisk();
      case '/' -> consumeSlash();
      case '%' -> consumePercent();
      case '&' -> consumeAmpersand();
      case '|' -> consumePipe();
      case '!' -> consumeBang();
      case '=' -> consumeEquals();
      case '<' -> consumeLess();
      case '>' -> consumeGreater();
      case '.' -> consumeDot();
      case '?' -> consumeQuestionMark();
      case ':' -> consumeColon();
      case ',' -> consumeComma();
      case '"' -> consumeQuote();
      case '{' -> consumeLeftBrace();
      case '}' -> consumeRightBrace();
      case '(' -> consumeLeftParenthesis();
      case ')' -> consumeRightParenthesis();
      case '[' -> consumeLeftSquareBracket();
      case ']' -> consumeRightSquareBracket();
      case '#' -> consumeHash();
      default -> consumeUnrecognisedCharacter();
    };
  }

  private Token newToken(TokenType type, int length) throws IOException {
    var location = context.charSource().location();
    var raw = context.charSource().readString(length);
    assert raw.length() == length : "EOF reached prematurely, missing check occurred elsewhere";
    return new SimpleToken(type, raw, location);
  }

  private Token newError(LexerError error, int length) throws IOException {
    var location = context.charSource().location();
    var raw = context.charSource().readString(length);
    assert raw.length() == length : "EOF reached prematurely, missing check occurred elsewhere";
    return new ErrorToken(error, raw, location);
  }

  private Token consumeNewLine() throws IOException {
    return switch (context.charSource().peek(0)) {
      case '\r' -> switch (context.charSource().peek(1)) {
        case '\n' -> newToken(TokenType.NEW_LINE, 2);
        default -> newError(LexerError.UNRECOGNISED_CHAR, 1);
      };
      case '\n' -> newToken(TokenType.NEW_LINE, 1);
      default -> newError(LexerError.UNRECOGNISED_CHAR, 1);
    };
  }

  private Token consumeIdentifier() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder();
    buff.append(context.charSource().read());

    while (true) {
      var next = context.charSource().peek(0);
      if (isIdContinue(next) || next == '-') {
        buff.append(next);
        context.charSource().advance(1);
      } else {
        break;
      }
    }

    return new SimpleToken(TokenType.IDENTIFIER, buff.raw(), location);
  }

  private Token consumeNumber() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder();

    tryConsumeIntegerPart(buff);

    if (context.charSource().peek(0) == '.') {
      tryConsumeFractionPart(buff);
    }

    var expPeek = context.charSource().peek(0);

    if (expPeek == 'e' || expPeek == 'E') {
      tryConsumeExponentPart(buff);
    }

    return new SimpleToken(TokenType.IDENTIFIER, buff.raw(), location);
  }

  private void tryConsumeIntegerPart(RawTokenBuilder buff) throws IOException {
    // We always consume as many digits as possible here.
    buff.append(context.charSource().read());

    while (isDigit(context.charSource().peek(0))) {
      buff.append(context.charSource().read());
    }
  }

  private void tryConsumeFractionPart(RawTokenBuilder buff) throws IOException {
    // We purposely don't consume the dot if we don't have a digit after it. That enables
    // us to have expressions like 123.name. Might be invalid later, but it gives better error
    // messages.
    if (!isDigit(context.charSource().peek(1))) {
      return;
    }

    // The dot.
    buff.append(context.charSource().read());

    // The digit part.
    tryConsumeIntegerPart(buff);
  }

  private void tryConsumeExponentPart(RawTokenBuilder buff) throws IOException {
    // We purposely don't consume the E if we don't have a + and digit or - and digit after it.
    // This enables us to treat other garbage as separate tokens to give better error messages.

    var secondChar = context.charSource().peek(1);
    var thirdChar = context.charSource().peek(2);

    if ((secondChar == '+' || secondChar == '-') && isDigit(thirdChar)) {
      // E+ or e+ or E- or e-.
      buff.append(context.charSource().readString(2));
      // The digit part.
      tryConsumeIntegerPart(buff);
    } else if (isDigit(secondChar)) {
      // The E or e.
      buff.append(context.charSource().read());
      tryConsumeIntegerPart(buff);
    }
  }

  private Token consumeEndOfFile() throws IOException {
    return newToken(TokenType.END_OF_FILE, 0);
  }

  private Token consumePlus() throws IOException {
    return newToken(TokenType.PLUS, 1);
  }

  private Token consumeMinus() throws IOException {
    return newToken(TokenType.MINUS, 1);
  }

  private Token consumeAsterisk() throws IOException {
    return newToken(TokenType.STAR, 1);
  }

  private Token consumePercent() throws IOException {
    return newToken(TokenType.MODULO, 1);
  }

  private Token consumeAmpersand() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '&' -> newToken(TokenType.AND, 2);
      default -> newError(LexerError.UNKNOWN_OPERATOR, 2);
    };
  }

  private Token consumeSlash() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '/' -> consumeLineComment();
      case '*' -> consumeInlineComment();
      default -> newToken(TokenType.DIVIDE, 1);
    };
  }

  private Token consumePipe() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '|' -> newToken(TokenType.OR, 2);
      default -> newError(LexerError.UNKNOWN_OPERATOR, 2);
    };
  }

  private Token consumeBang() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.NOT_EQUAL, 2);
      default -> newToken(TokenType.NOT, 1);
    };
  }

  private Token consumeEquals() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.EQUAL, 2);
      case '>' -> newToken(TokenType.FAT_ARROW, 2);
      default -> newToken(TokenType.ASSIGN, 1);
    };
  }

  private Token consumeLess() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '<' -> consumeHeredocAnchor();
      case '=' -> newToken(TokenType.LESS_EQUAL, 2);
      default -> newToken(TokenType.LESS, 1);
    };
  }

  private Token consumeGreater() throws IOException {
    return switch (context.charSource().peek(1)) {
      case '=' -> newToken(TokenType.GREATER_EQUAL, 2);
      default -> newToken(TokenType.GREATER, 1);
    };
  }

  private Token consumeDot() throws IOException {
    return context.charSource().startsWith("...")
        ? newToken(TokenType.ELLIPSIS, 3)
        : newToken(TokenType.DOT, 1);
  }

  private Token consumeQuestionMark() throws IOException {
    return newToken(TokenType.QUESTION_MARK, 1);
  }

  private Token consumeColon() throws IOException {
    return newToken(TokenType.COLON, 1);
  }

  private Token consumeComma() throws IOException {
    return newToken(TokenType.COMMA, 1);
  }

  private Token consumeQuote() throws IOException {
    // TODO: push quoted template mode.
    return newToken(TokenType.QUOTE, 1);
  }

  private Token consumeLeftBrace() throws IOException {
    // Push this mode. This can be overridden by a different block of logic for anything
    // subclassing or delegating to this lexer mode (e.g. template lexer modes). We push
    // this mode to enable popping it again afterwards when the block closes.
    context.pushMode(this);
    return newToken(TokenType.LEFT_BRACE, 1);
  }

  private Token consumeRightBrace() throws IOException {
    // Drop out of the current block, whatever that is.
    context.popMode();
    return newToken(TokenType.RIGHT_BRACE, 1);
  }

  private Token consumeLeftParenthesis() throws IOException {
    return newToken(TokenType.LEFT_PAREN, 1);
  }

  private Token consumeRightParenthesis() throws IOException {
    return newToken(TokenType.RIGHT_PAREN, 1);
  }

  private Token consumeLeftSquareBracket() throws IOException {
    return newToken(TokenType.LEFT_SQUARE, 1);
  }

  private Token consumeRightSquareBracket() throws IOException {
    return newToken(TokenType.RIGHT_SQUARE, 1);
  }

  private Token consumeHash() throws IOException {
    return consumeInlineComment();
  }

  private Token consumeLineComment() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder();
    buff.append(context.charSource().readString(2));

    while (true) {
      if (context.charSource().startsWith("*/")) {
        buff.append(context.charSource().readString(2));
        break;
      }

      var next = context.charSource().read();

      if (next == EOF) {
        // Provide an error on the next token.
        var error = newError(LexerError.UNEXPECTED_EOF_INLINE_COMMENT, 0);
        lookAheadQueue.add(error);
        break;
      }

      buff.append(next);
    }

    return new SimpleToken(TokenType.INLINE_COMMENT, buff.raw(), location);
  }

  private Token consumeInlineComment() throws IOException {
    var location = context.charSource().location();
    var buff = new RawTokenBuilder();

    if (context.charSource().startsWith("//")) {
      buff.append(context.charSource().readString(2));
    } else {
      buff.append(context.charSource().read());
    }

    while (true) {
      if (context.charSource().startsWith("\r\n")) {
        buff.append(context.charSource().readString(2));
        break;
      }

      var next = context.charSource().read();

      if (next == EOF) {
        break;
      }

      if (next == '\n') {
        buff.append(next);
        break;
      }

      buff.append(next);
    }

    return new SimpleToken(TokenType.LINE_COMMENT, buff.raw(), location);
  }

  private Token consumeHeredocAnchor() throws IOException {
    // We expect the following syntax for a heredoc to be valid:
    //
    // '<<' '-'? IDENTIFIER NEWLINE
    //
    // Spaces are not ignored here. Any deviation from this is a syntax error.
    //
    // We could have a custom lexer mode just to parse this header, but we can work around
    // this with a few conditional checks just as easily here, so let's avoid that for now.

    var anchor = context.charSource().startsWith("<<-")
        ? newToken(TokenType.HEREDOC_ANCHOR, 3)
        : newToken(TokenType.HEREDOC_ANCHOR, 2);

    if (context.charSource().peek(0) == '-') {
      // Next token must be an indent marker.
      lookAheadQueue.add(newToken(TokenType.HEREDOC_INDENT_MARKER, 1));
    }

    var idStart = context.charSource().peek(0);

    if (idStart == EOF) {
      var error = newError(LexerError.UNEXPECTED_EOF_HEREDOC_IDENTIFIER, 0);
      lookAheadQueue.add(error);

      return anchor;

    } else if (!isIdStart(idStart)) {
      var error = newError(LexerError.EXPECTED_HEREDOC_IDENTIFIER, 1);
      lookAheadQueue.add(error);

      return anchor;
    }

    var id = consumeIdentifier();
    lookAheadQueue.add(id);

    var newLine = consumeNewLine();

    if (newLine instanceof ErrorToken) {
      var error = new ErrorToken(
          LexerError.EXPECTED_NEW_LINE,
          newLine.raw(),
          newLine.location()
      );

      lookAheadQueue.add(error);
    } else {
      lookAheadQueue.add(newLine);
    }

    // TODO: push lexer mode.
    return anchor;
  }

  private Token consumeUnrecognisedCharacter() throws IOException {
    return newError(LexerError.UNRECOGNISED_CHAR, 1);
  }

  private static boolean isIdStart(int codePoint) {
    return Character.isUnicodeIdentifierStart(codePoint);
  }

  private static boolean isIdContinue(int codePoint) {
    return Character.isUnicodeIdentifierPart(codePoint);
  }

  private static boolean isDigit(int codePoint) {
    return '0' <= codePoint && codePoint <= '9';
  }
}
