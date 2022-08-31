package io.github.ascopes.hcl4j.core.inputs;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Representation of a location within an HCL file.
 *
 * @param fileName the file name.
 * @param position the 0-indexed position from the start of the file.
 * @param line     the 1-based line index.
 * @param column   the 1-based column index in the current line.
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.EXPERIMENTAL)
public record Location(
    String fileName,
    long position,
    long line,
    long column
) {

  @Override
  public String toString() {
    return fileName + "#L" + line + ":" + column;
  }
}
