module io.github.ascopes.hcl4j.core {
  requires java.base;
  requires static org.apiguardian.api;

  exports io.github.ascopes.hcl4j.core.annotations;
  exports io.github.ascopes.hcl4j.core.inputs;
  exports io.github.ascopes.hcl4j.core.lexer;
  exports io.github.ascopes.hcl4j.core.tokens;
}