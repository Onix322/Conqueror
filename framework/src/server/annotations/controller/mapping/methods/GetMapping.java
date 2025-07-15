package framework.src.server.annotations.controller.mapping.methods;

import framework.src.server.annotations.controller.mapping.Mapping;
import framework.src.server.httpServer.utils.httpMethod.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map HTTP GET requests onto specific handler methods.
 * This annotation can be used to specify the HTTP method and the path for the mapping.
 * By default, it maps to the GET method and the path "/get".
 */
@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    HttpMethod httpMethod() default HttpMethod.GET;
    String value() default "/get";
}
