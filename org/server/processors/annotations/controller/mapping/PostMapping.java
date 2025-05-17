package org.server.processors.annotations.controller.mapping;

import org.server.httpServer.HttpMethod;

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
