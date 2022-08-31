package io.github.ascopes.hcl4j.core.tokens.impl;

import io.github.ascopes.hcl4j.core.inputs.Location;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Standard representation of token that represents part of an HCL file.
 *
 * @param type     the token type.
 * @param raw      the token content.
 * @param location the location in the file.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
public record SimpleToken(
    TokenType type,
    CharSequence raw,
    Location location
) implements Token {

}
