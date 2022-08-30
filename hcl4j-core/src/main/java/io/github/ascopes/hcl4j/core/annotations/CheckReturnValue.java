package io.github.ascopes.hcl4j.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply to methods to mark their return value as being something that
 * should not be discarded immediately.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface CheckReturnValue {
}
