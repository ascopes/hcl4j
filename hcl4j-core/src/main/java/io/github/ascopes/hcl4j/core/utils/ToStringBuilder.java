package io.github.ascopes.hcl4j.core.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Builder for producing consistent {@code toString} methods on data classes.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
public final class ToStringBuilder {

  private final Object self;
  private final Map<String, Object> attrs;

  /**
   * Initialize the new builder.
   *
   * @param self the object that {@code toString} was called on.
   */
  public ToStringBuilder(Object self) {
    this.self = self;
    attrs = new TreeMap<>();
  }

  /**
   * Add an attribute.
   *
   * @param name the attribute name.
   * @param value the attribute value.
   * @return this builder.
   */
  public ToStringBuilder add(String name, Object value) {
    attrs.put(name, value);
    return this;
  }

  /**
   * Build the string representation.
   *
   * @return the string representation.
   */
  @Override
  public String toString() {
    var builder = new StringBuilder()
        .append(self.getClass().getSimpleName())
        .append("{");

    var attrIter = attrs.entrySet().iterator();

    while (attrIter.hasNext()) {
      var attr = attrIter.next();
      builder.append(attr.getKey()).append('=');
      toStringElement(builder, attr.getValue());
      if (attrIter.hasNext()) {
        builder.append(", ");
      }
    }

    return builder.append('}').toString();
  }

  private static void toStringElement(StringBuilder builder, Object object) {
    if (object == null) {
      builder.append("null");
      return;
    }

    if (object.getClass().isArray()) {
      toStringArray(builder, object);
      return;
    }

    if (object instanceof Collection) {
      toStringCollection(builder, (Collection<?>) object);
      return;
    }

    if (object instanceof Character) {
      toStringChar(builder, (char) object);
      return;
    }

    if (object instanceof CharSequence) {
      toStringCharSequence(builder, (CharSequence) object);
      return;
    }

    // Give up.
    builder.append(object);
  }

  private static void toStringArray(StringBuilder builder, Object array) {
    builder.append("[");

    for (var i = 0; i < Array.getLength(array); ++i) {
      if (i > 0) {
        builder.append(", ");
      }

      var next = Array.get(array, i);
      toStringElement(builder, next);
    }

    builder.append("]");
  }

  private static void toStringCollection(StringBuilder builder, Collection<?> collection) {
    builder.append("[");

    var iter = collection.iterator();

    while (iter.hasNext()) {
      toStringElement(builder, iter.next());
      if (iter.hasNext()) {
        builder.append(", ");
      }
    }

    builder.append("]");
  }

  private static void toStringChar(StringBuilder builder, char chr) {
    builder.append('\'');
    escapeChar(builder, chr, '\'');
    builder.append('\'');
  }

  private static void toStringCharSequence(StringBuilder builder, CharSequence string) {
    builder.append('"');
    for (var i = 0; i < string.length(); ++i) {
      escapeChar(builder, string.charAt(i), '"');
    }
    builder.append('"');
  }

  private static void escapeChar(StringBuilder builder, char c, char quoteChar) {
    switch (c) {
      case '\0':
        builder.append("\\0");
        return;
      case '\\':
        builder.append("\\\\");
        return;
      case '\r':
        builder.append("\\r");
        return;
      case '\n':
        builder.append("\\n");
        return;
      case '\t':
        builder.append("\\t");
        return;
    }

    if (c == quoteChar) {
      builder.append("\\").append(c);
    }

    if (c >= 0x20 && c < 0x7F || c >= 0x80 && c <= 0xFF) {
      builder.append(c);
      return;
    }

    builder.append(String.format("\\u%04x", (int) c));
  }
}
