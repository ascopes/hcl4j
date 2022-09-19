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

package io.github.ascopes.hcl4j.core.parser;

import io.github.ascopes.hcl4j.core.ast.HclBodyItemNode;
import io.github.ascopes.hcl4j.core.ast.HclBodyItemNode.HclAttributeNode;
import io.github.ascopes.hcl4j.core.ast.HclBodyItemNode.HclBlockNode;
import io.github.ascopes.hcl4j.core.ast.HclBodyNode;
import io.github.ascopes.hcl4j.core.ast.HclCollectionValueNode;
import io.github.ascopes.hcl4j.core.ast.HclCollectionValueNode.HclObjectElementNode;
import io.github.ascopes.hcl4j.core.ast.HclCollectionValueNode.HclObjectNode;
import io.github.ascopes.hcl4j.core.ast.HclCollectionValueNode.HclTupleElementNode;
import io.github.ascopes.hcl4j.core.ast.HclCollectionValueNode.HclTupleNode;
import io.github.ascopes.hcl4j.core.ast.HclConditionalNode;
import io.github.ascopes.hcl4j.core.ast.HclExprTermNode;
import io.github.ascopes.hcl4j.core.ast.HclExpressionNode;
import io.github.ascopes.hcl4j.core.ast.HclForExprNode;
import io.github.ascopes.hcl4j.core.ast.HclForExprNode.HclForConditionNode;
import io.github.ascopes.hcl4j.core.ast.HclForExprNode.HclForIntroNode;
import io.github.ascopes.hcl4j.core.ast.HclForExprNode.HclForObjectExprNode;
import io.github.ascopes.hcl4j.core.ast.HclForExprNode.HclForTupleExprNode;
import io.github.ascopes.hcl4j.core.ast.HclFunctionCallNode;
import io.github.ascopes.hcl4j.core.ast.HclFunctionCallNode.HclParameterNode;
import io.github.ascopes.hcl4j.core.ast.HclGetAttrNode;
import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode;
import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode.HclIdentifierNode;
import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode.HclStringLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclIndexNode;
import io.github.ascopes.hcl4j.core.ast.HclLegacyIndexNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclBooleanLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclIntegerLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclNullLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclRealLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclOperationNode.HclBinaryOperationNode;
import io.github.ascopes.hcl4j.core.ast.HclOperationNode.HclUnaryOperationNode;
import io.github.ascopes.hcl4j.core.ast.HclSplatNode.HclAttrSplatNode;
import io.github.ascopes.hcl4j.core.ast.HclSplatNode.HclFullSplatNode;
import io.github.ascopes.hcl4j.core.ast.HclTemplateExprNode;
import io.github.ascopes.hcl4j.core.ast.HclVariableExprNode;
import io.github.ascopes.hcl4j.core.ast.HclWrappedExpressionNode;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.tokens.HclToken;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Implementation of the bulk of the HCL parser functionality without defining the root node
 * entrypoint.
 *
 * <p>Classes can use this implementation or define their own parsing mechanism.
 *
 * <p>This implementation is an {@code LL(k)} recursive-descent parser.
 *
 * @param <T> the root node type.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public abstract class HclDefaultParserBase<T> implements HclParser<T> {

  protected final HclTokenStream tokenStream;

  /**
   * Initialize the parser base.
   *
   * @param tokenStream the token stream to use.
   */
  public HclDefaultParserBase(HclTokenStream tokenStream) {
    this.tokenStream = tokenStream;

    tokenStream.ignoreToken(HclTokenType.WHITESPACE);
    tokenStream.ignoreToken(HclTokenType.INLINE_COMMENT_START);
    tokenStream.ignoreToken(HclTokenType.INLINE_COMMENT_END);
    tokenStream.ignoreToken(HclTokenType.LINE_COMMENT_HASH_START);
    tokenStream.ignoreToken(HclTokenType.LINE_COMMENT_SLASH_START);
    tokenStream.ignoreToken(HclTokenType.COMMENT_CONTENT);
  }

  /**
   * Parse an identifier-like literal.
   *
   * <pre><code>
   *   identifierLike = identifier | stringLit ;
   *   stringLit = OPENING_QUOTE RAW_TEXT? CLOSING_QUOTE ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclIdentifierLikeNode identifierOrStringLit() {
    if (tokenStream.peek(0).type() == HclTokenType.IDENTIFIER) {
      return identifier();
    }

    var openingQuote = tokenStream.eat(HclTokenType.OPENING_QUOTE);

    var content = tokenStream.peek(0).type() == HclTokenType.CLOSING_QUOTE
        ? null
        : tokenStream.eat(HclTokenType.RAW_TEXT);

    var closingQuote = tokenStream.eat(HclTokenType.CLOSING_QUOTE);

    return new HclStringLiteralNode(openingQuote, content, closingQuote);
  }

  /**
   * Parse an identifier.
   *
   * <pre><code>
   *   identifier = IDENTIFIER ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclIdentifierNode identifier() throws HclProcessingException {
    return new HclIdentifierNode(tokenStream.eat(HclTokenType.IDENTIFIER));
  }

  /**
   * Parse a body.
   *
   * <pre><code>
   *   body = attribute | block ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclBodyNode body() {
    return tokenStream.scoped(() -> {
      tokenStream.ignoreToken(HclTokenType.NEW_LINE);

      var start = tokenStream.location();
      var items = new ArrayList<HclBodyItemNode>();

      while (tokenStream.peek(0).type() == HclTokenType.IDENTIFIER) {
        var nextItem = tokenStream.peek(1).type() == HclTokenType.ASSIGN
            ? attribute()
            : block();

        items.add(nextItem);
      }

      var end = tokenStream.location();

      return new HclBodyNode(items, start, end);
    });
  }

  /**
   * Parse an attribute.
   *
   * <pre><code>
   *   attribute = identifier , ASSIGN , expression ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclAttributeNode attribute() {
    var identifier = identifier();
    var assignToken = tokenStream.eat(HclTokenType.ASSIGN);
    var expression = expr();
    return new HclAttributeNode(identifier, assignToken, expression);
  }

  /**
   * Parse a block.
   *
   * <pre><code>
   *   block = identifier , identifierLike* , ( oneLineBlock | multiLineBlock );
   *   oneLineBlock = LEFT_BRACE , attribute , RIGHT_BRACE , NEW_LINE ;
   *   multiLineBlock = LEFT_BRACE , NEW_LINE , body , RIGHT_BRACE , NEW_LINE ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclBlockNode block() {
    var firstIdentifier = identifier();

    var additionalIdentifiers = new ArrayList<HclIdentifierLikeNode>();

    while (true) {
      var nextType = tokenStream.peek(0).type();
      if (nextType == HclTokenType.IDENTIFIER || nextType == HclTokenType.OPENING_QUOTE) {
        additionalIdentifiers.add(identifierOrStringLit());
      } else {
        break;
      }
    }

    var leftBrace = tokenStream.eat(HclTokenType.LEFT_BRACE);

    tokenStream.eatIfMatches(HclTokenType.NEW_LINE);

    // Body will pick up both the single-attribute case that makes up a one-line block,
    // and the nested body case for multi-line blocks.
    var body = body();

    var rightBrace = tokenStream.eat(HclTokenType.RIGHT_BRACE);
    tokenStream.eat(HclTokenType.NEW_LINE);

    return new HclBlockNode(
        firstIdentifier,
        additionalIdentifiers,
        leftBrace,
        body,
        rightBrace
    );
  }

  /**
   * Parse an expression node that can have a conditional at the end.
   *
   * <pre><code>
   *   expr = operation , QUESTION_MARK , expr , COLON , expr
   *        | operation
   *        ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclExpressionNode expr() {
    var expr = orOp();

    if (tokenStream.peek(0).type() != HclTokenType.QUESTION_MARK) {
      return expr;
    }

    var questionMark = tokenStream.eat(HclTokenType.QUESTION_MARK);
    var trueExpr = expr();
    var colon = tokenStream.eat(HclTokenType.COLON);
    var falseExpr = expr();

    return new HclConditionalNode(expr, questionMark, trueExpr, colon, falseExpr);
  }

  /**
   * Parse an operation that may contain an OR after it.
   *
   * <pre><code>
   *   orOp = andOp , OR , orOp
   *        | andOp
   *        ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode orOp() {
    var left = andOp();

    if (tokenStream.peek(0).type() == HclTokenType.OR) {
      var operator = tokenStream.eat(HclTokenType.OR);
      var right = orOp();
      return new HclBinaryOperationNode(left, operator, right);
    }

    return left;
  }

  /**
   * Parse an operation that may contain an AND after it.
   *
   * <pre><code>
   *   andOp = eqOp , AND , andOp
   *         | eqOp
   *         ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode andOp() {
    var left = eqOp();

    if (tokenStream.peek(0).type() == HclTokenType.AND) {
      var operator = tokenStream.eat(HclTokenType.AND);
      var right = andOp();
      return new HclBinaryOperationNode(left, operator, right);
    }

    return left;
  }

  /**
   * Parse an operation that may contain an equality or inequality operation after it.
   *
   * <pre><code>
   *   eqOp = compOp , EQUAL , eqOp
   *        | compOp , NOT_EQUAL , eqOp
   *        | compOp
   *        ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode eqOp() {
    var left = compOp();
    var next = tokenStream.peek(0).type();

    return switch (next) {
      case EQUAL, NOT_EQUAL -> {
        var operator = tokenStream.eat(next);
        var right = eqOp();
        yield new HclBinaryOperationNode(left, operator, right);
      }
      default -> left;
    };
  }

  /**
   * Parse an operation that may contain a comparative operation after it.
   *
   * <pre><code>
   *   compOp = addOp , LESS , compOp
   *          | addOp , LESS_EQUAL , compOp
   *          | addOp , GREATER , compOp
   *          | addOp , GREATER_EQUAL , compOp
   *          | addOp
   *          ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode compOp() {
    var left = addOp();
    var next = tokenStream.peek(0).type();

    return switch (next) {
      case LESS, GREATER, LESS_EQUAL, GREATER_EQUAL -> {
        var operator = tokenStream.eat(next);
        var right = compOp();
        yield new HclBinaryOperationNode(left, operator, right);
      }
      default -> left;
    };
  }

  /**
   * Parse an operation that may contain an additive operation after it.
   *
   * <pre><code>
   *   addOp = mulOp , PLUS , addOp
   *         | mulOp , MINUS , addOp
   *         | mulOp
   *         ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode addOp() {
    var left = mulOp();
    var next = tokenStream.peek(0).type();

    return switch (next) {
      case PLUS, MINUS -> {
        var operator = tokenStream.eat(next);
        var right = addOp();
        yield new HclBinaryOperationNode(left, operator, right);
      }
      default -> left;
    };
  }

  /**
   * Parse an operation that may contain a multiplicative operation after it.
   *
   * <pre><code>
   *   mulOp = unaryOp , STAR , mulOp
   *         | unaryOp , DIVIDE , mulOp
   *         | unaryOp , MODULO , mulOp
   *         | unaryOp
   *         ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode mulOp() {
    var left = unaryOp();
    var next = tokenStream.peek(0).type();

    return switch (next) {
      case STAR, DIVIDE, MODULO -> {
        var operator = tokenStream.eat(next);
        var right = mulOp();
        yield new HclBinaryOperationNode(left, operator, right);
      }
      default -> left;
    };
  }

  /**
   * Parse an operation that may be a unary operator or just an expression term.
   *
   * <pre><code>
   *   unaryOp = MINUS , unaryOp
   *           | NOT , unaryOp
   *           | exprTerm
   *           ;
   * </code></pre>
   *
   * @return the operation.
   */
  protected HclExpressionNode unaryOp() {
    var next = tokenStream.peek(0).type();

    return switch (next) {
      case MINUS, NOT -> {
        var operator = tokenStream.eat(next);
        var right = unaryOp();
        yield new HclUnaryOperationNode(operator, right);
      }
      default -> exprTerm();
    };
  }

  /**
   * Parse an expression term that may have an optional chained index, attribute getter, or splat on
   * the end.
   *
   * <pre><code>
   *   exprTerm = splattableExprTerm , legacyIndex
   *            | splattableExprTerm , (getAttr | index)+ ;
   *            | splattableExprTerm
   *            ;
   *
   *   legacyIndex = DOT , INTEGER ;
   *   getAttr = DOT , IDENTIFIER ;
   *   index = LEFT_SQUARE , expr , RIGHT_SQUARE ;
   * </code></pre>
   *
   * @return the parsed expression term.
   */
  protected HclExprTermNode exprTerm() {
    var exprTerm = splattableExprTerm();

    loop:
    while (true) {
      var firstToken = tokenStream.peek(0);
      var secondToken = tokenStream.peek(1);

      switch (firstToken.type()) {
        case LEFT_SQUARE -> {
          var open = tokenStream.eat(HclTokenType.LEFT_SQUARE);
          var expr = expr();
          var close = tokenStream.eat(HclTokenType.RIGHT_SQUARE);
          exprTerm = new HclIndexNode(exprTerm, open, expr, close);
        }

        case DOT -> {
          switch (secondToken.type()) {
            case INTEGER -> {
              var dot = tokenStream.eat(HclTokenType.DOT);
              var valueToken = tokenStream.eat(HclTokenType.INTEGER);
              var value = new BigInteger(valueToken.raw().toString());
              var index = new HclIntegerLiteralNode(valueToken, value);
              return new HclLegacyIndexNode(exprTerm, dot, index);
            }

            case IDENTIFIER -> {
              var dot = tokenStream.eat(HclTokenType.DOT);
              var attr = identifier();
              exprTerm = new HclGetAttrNode(exprTerm, dot, attr);
            }

            default -> {
              break loop;
            }
          }
        }

        default -> {
          break loop;
        }
      }
    }

    return exprTerm;
  }

  /**
   * Parse a splattable expression term.
   *
   * <pre><code>
   *   splattableExprTerm = singleExprTerm , LEFT_SQUARE , STAR , RIGHT_SQUARE
   *                      | singleExprTerm , DOT , STAR
   *                      | singleExprTerm
   *                      ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclExprTermNode splattableExprTerm() {
    var exprTerm = singleExprTerm();

    var firstToken = tokenStream.peek(0);
    var secondToken = tokenStream.peek(1);

    if ((firstToken.type() == HclTokenType.LEFT_SQUARE || firstToken.type() == HclTokenType.DOT)
        && secondToken.type() == HclTokenType.STAR) {
      var open = tokenStream.eat(HclTokenType.DOT, HclTokenType.LEFT_SQUARE);
      var splat = tokenStream.eat(HclTokenType.STAR);

      if (open.type() == HclTokenType.LEFT_SQUARE) {
        var close = tokenStream.eat(HclTokenType.RIGHT_SQUARE);
        return new HclFullSplatNode(exprTerm, open, splat, close);
      }

      return new HclAttrSplatNode(exprTerm, open, splat);
    }

    return exprTerm;
  }

  /**
   * Parse a singular expression term.
   *
   * <pre><code>
   *   singleExprTerm = realLit
   *                  | integerLit
   *                  | softKeywordLit
   *                  | functionCall
   *                  | forExpr
   *                  | collectionValue
   *                  | templateExpr
   *                  | variableExpr
   *                  | wrappedExpr
   *                  ;
   *
   *   realLit = REAL ;
   *   integerLit = INTEGER ;
   *   softKeywordLit = TRUE | FALSE | NULL ;
   *   TRUE = "true" (* identifier with specific content *) ;
   *   FALSE = "false" (* identifier with specific content *) ;
   *   NULL = "null" (* identifier with specific content *) ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclExprTermNode singleExprTerm() {
    var token = tokenStream.peek(0);

    return switch (token.type()) {
      case REAL -> {
        var value = new BigDecimal(token.raw().toString());
        yield new HclRealLiteralNode(tokenStream.eat(HclTokenType.REAL), value);
      }

      case INTEGER -> {
        var value = new BigInteger(token.raw().toString());
        yield new HclIntegerLiteralNode(tokenStream.eat(HclTokenType.INTEGER), value);
      }

      case IDENTIFIER -> {
        if (tokenStream.peek(0).type() == HclTokenType.LEFT_PAREN) {
          yield functionCall();
        }

        if (token.rawEquals("true")) {
          yield new HclBooleanLiteralNode(tokenStream.eat(HclTokenType.IDENTIFIER), true);
        }

        if (token.rawEquals("false")) {
          yield new HclBooleanLiteralNode(tokenStream.eat(HclTokenType.IDENTIFIER), false);
        }

        if (token.rawEquals("null")) {
          yield new HclNullLiteralNode(tokenStream.eat(HclTokenType.IDENTIFIER));
        }

        yield variableExpr();
      }

      case LEFT_SQUARE, LEFT_BRACE -> token.rawEquals("for")
          ? forExpr()
          : collectionValue();

      case LEFT_PAREN -> wrappedExpr();

      default -> templateExpr();
    };
  }

  /**
   * Parse a function call node.
   *
   * <pre><code>
   *   functionCall = identifier , LEFT_PAREN , RIGHT_PAREN
   *                | identifier , LEFT_PAREN , arguments , trailer? , RIGHT_PAREN
   *                ;
   *
   *   arguments = expr , ( COMMA expr )* ;
   *
   *   trailer = COMMA | ellipsis ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclFunctionCallNode functionCall() {
    var identifier = identifier();
    var leftParen = tokenStream.eat(HclTokenType.LEFT_PAREN);

    // Scoping needed to work around changing newline rules.
    // TODO(ascopes): simplify this, where possible.
    return tokenStream.scoped(() -> {
      tokenStream.ignoreToken(HclTokenType.NEW_LINE);

      var arguments = new ArrayList<HclParameterNode>();

      if (tokenStream.peek(0).type() != HclTokenType.RIGHT_PAREN) {
        var firstExpr = tokenStream.scoped(() -> {
          tokenStream.unignoreToken(HclTokenType.NEW_LINE);
          return expr();
        });

        arguments.add(new HclParameterNode(null, firstExpr));

        while (true) {
          if (tokenStream.peek(0).type() != HclTokenType.COMMA) {
            break;
          }

          var afterComma = tokenStream.peek(1);

          if (afterComma.type() == HclTokenType.ELLIPSIS
              || afterComma.type() == HclTokenType.RIGHT_PAREN) {
            break;
          }

          var expr = tokenStream.scoped(() -> {
            tokenStream.unignoreToken(HclTokenType.NEW_LINE);
            return expr();
          });

          arguments.add(new HclParameterNode(tokenStream.eat(HclTokenType.COMMA), expr));
        }
      }

      var trailer = arguments.isEmpty()
          ? tokenStream.eatIfMatches(HclTokenType.ELLIPSIS, HclTokenType.COMMA)
          : null;

      var rightParen = tokenStream.eat(HclTokenType.RIGHT_PAREN);

      return new HclFunctionCallNode(identifier, leftParen, arguments, trailer, rightParen);
    });
  }

  /**
   * Parse a variable expression.
   *
   * <pre><code>
   *   variableExpr = identifier ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclVariableExprNode variableExpr() {
    return new HclVariableExprNode(identifier());
  }

  /**
   * Parse an object or tuple literal.
   *
   * <pre><code>
   *   collectionValue = tuple | object ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclCollectionValueNode collectionValue() {
    return tokenStream.peek(0).type() == HclTokenType.LEFT_SQUARE
        ? tuple()
        : object();
  }

  /**
   * Parse a tuple literal.
   *
   * <pre><code>
   *   tuple = LEFT_SQUARE , RIGHT_SQUARE
   *         | LEFT_SQUARE , expr , ( COMMA , expr )* , COMMA? , RIGHT_SQUARE
   *         ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclTupleNode tuple() {
    var leftToken = tokenStream.eat(HclTokenType.LEFT_SQUARE);
    var elements = new ArrayList<HclTupleElementNode>();

    if (tokenStream.peek(0).type() != HclTokenType.RIGHT_SQUARE) {
      elements.add(new HclTupleElementNode(null, expr()));

      while (true) {
        if (tokenStream.peek(0).type() != HclTokenType.COMMA) {
          break;
        }

        var afterComma = tokenStream.peek(1);

        if (afterComma.type() == HclTokenType.RIGHT_SQUARE) {
          break;
        }

        var commaToken = tokenStream.eatIfMatches(HclTokenType.COMMA);
        var expression = expr();
        elements.add(new HclTupleElementNode(commaToken, expression));
      }
    }

    var trailerComma = elements.isEmpty()
        ? null
        : tokenStream.eatIfMatches(HclTokenType.COMMA);

    var rightToken = tokenStream.eat(HclTokenType.RIGHT_SQUARE);

    return new HclTupleNode(leftToken, elements, trailerComma, rightToken);
  }

  /**
   * Parse an object literal.
   *
   * <pre><code>
   *   object = LEFT_BRACE , RIGHT_BRACE
   *          | LEFT_BRACE , objectElem+ , COMMA? , RIGHT_BRACE
   *          ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclObjectNode object() {
    var leftToken = tokenStream.eat(HclTokenType.LEFT_BRACE);
    var elements = new ArrayList<HclObjectElementNode>();

    if (tokenStream.peek(0).type() != HclTokenType.RIGHT_BRACE) {
      elements.add(objectElem(true));

      while (true) {
        var afterComma = tokenStream.peek(1);

        if (afterComma.type() == HclTokenType.RIGHT_BRACE) {
          break;
        }

        elements.add(objectElem(false));
      }
    }

    var trailerComma = elements.isEmpty()
        ? null
        : tokenStream.eatIfMatches(HclTokenType.COMMA);

    var rightToken = tokenStream.eat(HclTokenType.RIGHT_BRACE);

    return new HclObjectNode(leftToken, elements, trailerComma, rightToken);
  }

  /**
   * Parse an object element.
   *
   * <pre><code>
   *   objectElem = (variableExpr | expr) , (ASSIGN | COLON) , expr ;
   * </code></pre>
   *
   * <p>If the left-hand-side is a {@link #variableExpr()}, then the identifier is used as-is as
   * the
   * key. If it is anything else, then the key is evaluated as a {@link #expr()} to get the actual
   * key to use by the interpreter later.
   *
   * @param first if {@code true}, then a leading comma is not allowed.
   * @return the node.
   */
  protected HclObjectElementNode objectElem(boolean first) {
    var commaToken = first ? null : tokenStream.eatIfMatches(HclTokenType.COMMA);
    var keyIsExpression = tokenStream.peek(0).type() == HclTokenType.IDENTIFIER;
    var keyExpression = expr();
    var mapperToken = tokenStream.eat(HclTokenType.ASSIGN, HclTokenType.COLON);
    var valueExpression = expr();

    return new HclObjectElementNode(
        commaToken,
        keyExpression,
        keyIsExpression,
        mapperToken,
        valueExpression
    );
  }

  protected HclTemplateExprNode templateExpr() {
    throw new UnsupportedOperationException();
  }

  /**
   * Parse a for-expression.
   *
   * <pre><code>
   *   forExpr = forTupleExpr | forObjectExpr ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclForExprNode forExpr() {
    return tokenStream.peek(0).type() == HclTokenType.LEFT_SQUARE
        ? forTupleExpr()
        : forObjectExpr();
  }

  /**
   * Parse a for-tuple expression.
   *
   * <pre><code>
   *   forTupleExpr = LEFT_SQUARE , forIntro , expr , forCond? , RIGHT_SQUARE ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclForTupleExprNode forTupleExpr() {
    var leftToken = tokenStream.eat(HclTokenType.LEFT_SQUARE);
    var intro = forIntro();
    var expression = expr();
    var forCondition = tokenStream.peek(0).type() == HclTokenType.IDENTIFIER
        ? forCond()
        : null;
    var rightToken = tokenStream.eat(HclTokenType.RIGHT_SQUARE);

    return new HclForTupleExprNode(
        leftToken,
        intro,
        expression,
        forCondition,
        rightToken
    );
  }

  /**
   * Parse a for-object expression.
   *
   * <pre><code>
   *   forObjectExpr = LEFT_BRACE , forIntro , expr , FAT_ARROW
   *                 , expr , ELLIPSIS? , forCond? , RIGHT_BRACE
   *                 ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclForObjectExprNode forObjectExpr() {
    var leftToken = tokenStream.eat(HclTokenType.LEFT_BRACE);
    var intro = forIntro();
    var keyExpression = expr();
    var fatArrowToken = tokenStream.eat(HclTokenType.FAT_ARROW);
    var valueExpression = expr();
    var ellipsisToken = tokenStream.eatIfMatches(HclTokenType.ELLIPSIS);
    var forCondition = tokenStream.peek(0).type() == HclTokenType.IDENTIFIER
        ? forCond()
        : null;
    var rightToken = tokenStream.eat(HclTokenType.RIGHT_BRACE);

    return new HclForObjectExprNode(
        leftToken,
        intro,
        keyExpression,
        fatArrowToken,
        valueExpression,
        ellipsisToken,
        forCondition,
        rightToken
    );
  }

  /**
   * Parse a for-intro.
   *
   * <pre><code>
   *   forIntro = FOR , identifier , ( COMMA , identifier )? , IN , expr , COLON ;
   *   FOR = "for" (* identifier with specific content *) ;
   *   IN = "in" (* identifier with specific content *) ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclForIntroNode forIntro() {
    var forToken = tokenStream.eatKeyword("for");
    var firstIdentifier = identifier();

    HclToken commaToken = null;
    HclIdentifierNode secondIdentifier = null;

    if (tokenStream.peek(0).type() == HclTokenType.COMMA) {
      commaToken = tokenStream.eat(HclTokenType.COMMA);
      secondIdentifier = identifier();
    }

    var inToken = tokenStream.eatKeyword("in");
    var inExpression = expr();
    var colonToken = tokenStream.eat(HclTokenType.COLON);

    return new HclForIntroNode(
        forToken,
        firstIdentifier,
        commaToken,
        secondIdentifier,
        inToken,
        inExpression,
        colonToken
    );
  }

  /**
   * Parse a for-condition.
   *
   * <pre><code>
   *   forCond = IF , expr ;
   *   IF = "if" (* identifier with specific content *) ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclForConditionNode forCond() {
    var ifToken = tokenStream.eatKeyword("if");
    var ifExpression = expr();
    return new HclForConditionNode(ifToken, ifExpression);
  }

  /**
   * Parse a wrapped expression node.
   *
   * <pre><code>
   *   wrappedExpr = LEFT_PAREN , expr , RIGHT_PAREN ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclWrappedExpressionNode wrappedExpr() {
    var left = tokenStream.eat(HclTokenType.LEFT_PAREN);

    return tokenStream.scoped(() -> {
      tokenStream.ignoreToken(HclTokenType.NEW_LINE);
      var expr = expr();
      var right = tokenStream.eat(HclTokenType.RIGHT_PAREN);

      return new HclWrappedExpressionNode(left, expr, right);
    });
  }
}
