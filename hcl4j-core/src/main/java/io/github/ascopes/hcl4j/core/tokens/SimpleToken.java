package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.inputs.Location;

/**
 * Standard representation of token that represents part of an HCL file.
 *
 * @param type     the token type.
 * @param raw      the token content.
 * @param location the location in the file.
 * @author Ashley Scopes
 * @since 0.0.1
 */
public record SimpleToken(
    TokenType type,
    CharSequence raw,
    Location location
) implements Token {

}
