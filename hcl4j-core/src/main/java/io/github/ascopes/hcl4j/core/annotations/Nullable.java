package io.github.ascopes.hcl4j.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * Annotation that marks the annotated element as having a potentially null value.
 *
 * @author Ashley Scopes
 * @since 0.0.1
 */
@API(since = "0.0.1", status = Status.INTERNAL)
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.ANNOTATION_TYPE,
    ElementType.FIELD,
    ElementType.LOCAL_VARIABLE,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.RECORD_COMPONENT,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
})
public @interface Nullable {

}
