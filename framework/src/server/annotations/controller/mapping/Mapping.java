package framework.src.server.annotations.controller.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark other annotations as mapping annotations.
 * It is typically used in the context of web frameworks to indicate that
 * the annotated annotation is responsible for mapping HTTP requests to
 * specific handler methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Mapping {
}
