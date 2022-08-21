package io.github.ascopes.hcl4j.core.tokens;

import io.github.ascopes.hcl4j.core.lexer.Location;
import io.github.ascopes.hcl4j.core.utils.ToStringBuilder;

public class QuotedAnchor extends AbstractToken {

  public QuotedAnchor(Location location, int rawChar) {
    super(location, rawChar);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .add("location", getLocation())
        .toString();
  }
}
