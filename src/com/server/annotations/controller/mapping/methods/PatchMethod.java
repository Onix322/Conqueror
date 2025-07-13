package src.com.server.annotations.controller.mapping.methods;

import src.com.server.annotations.controller.mapping.Mapping;
import src.com.server.httpServer.utils.httpMethod.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map HTTP PATCH requests to a method in a controller.
 * This annotation can be used to specify the HTTP method and the path for the mapping.
 * By default, it maps to the PATCH method and the path "/patch".
 */
@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PatchMethod {
    HttpMethod httpMethod() default HttpMethod.PATCH;
    String value() default "/patch";
}
