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
import io.github.ascopes.hcl4j.core.ast.HclExpressionNode;
import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode;
import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode.HclIdentifierNode;
import io.github.ascopes.hcl4j.core.ast.HclIdentifierLikeNode.HclStringLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclIntegerLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclNumericLiteralNode;
import io.github.ascopes.hcl4j.core.ast.HclLiteralValueNode.HclRealLiteralNode;
import io.github.ascopes.hcl4j.core.ex.HclProcessingException;
import io.github.ascopes.hcl4j.core.tokens.HclTokenType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
  protected HclIdentifierLikeNode identifier() throws HclProcessingException {
    return new HclIdentifierNode(tokenStream.eat(HclTokenType.IDENTIFIER));
  }

  /**
   * Parse a numeric literal.
   *
   * <pre><code>
   *   numericLit = INTEGER | REAL ;
   * </code></pre>
   *
   * @return the node.
   */
  protected HclNumericLiteralNode<?> numericLit() {
    var token = tokenStream.eat(HclTokenType.INTEGER, HclTokenType.REAL);

    if (token.type() == HclTokenType.INTEGER) {
      var value = new BigInteger(token.raw().toString());
      return new HclIntegerLiteralNode(token, value);
    } else {
      var value = new BigDecimal(token.raw().toString());
      return new HclRealLiteralNode(token, value);
    }
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
    var start = tokenStream.location();
    var items = new ArrayList<HclBodyItemNode>();

    while (tokenStream.peek(0).type() == HclTokenType.IDENTIFIER) {
      var nextItem = tokenStream.peek(1).type() == HclTokenType.ASSIGN
          ? attribute()
          : block();

      items.add(nextItem);
    }

    var end = tokenStream.location();

    // Create an immutable copy.
    return new HclBodyNode(List.copyOf(items), start, end);
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
    var expression = expression();
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

    if (tokenStream.peek(0).type() == HclTokenType.NEW_LINE) {
      tokenStream.eat(HclTokenType.NEW_LINE);
    }

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
   * TODO: implement me.
   *
   * @return nothing, this is not implemented yet.
   */
  protected HclExpressionNode expression() {
    throw new UnsupportedOperationException();
  }
}
