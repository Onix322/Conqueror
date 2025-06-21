package src.com.server.annotations.controller.mapping.methods;

import src.com.server.annotations.controller.mapping.Mapping;
import src.com.server.httpServer.utils.httpMethod.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostMapping {
    HttpMethod httpMethod() default HttpMethod.POST;
    String value() default "/post";
}
