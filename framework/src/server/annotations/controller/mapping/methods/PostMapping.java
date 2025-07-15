package framework.src.server.annotations.controller.mapping.methods;

import framework.src.server.annotations.controller.mapping.Mapping;
import framework.src.server.httpServer.utils.httpMethod.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping HTTP POST requests onto specific handler methods.
 * This annotation can be used to specify the HTTP method and the path for the request.
 * It is typically used in web applications to handle form submissions or data creation.
 */
@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostMapping {
    HttpMethod httpMethod() default HttpMethod.POST;
    String value() default "/post";
}
