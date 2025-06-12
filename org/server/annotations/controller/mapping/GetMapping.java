package org.server.annotations.controller.mapping;

import org.server.httpServer.utils.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    HttpMethod httpMethod() default HttpMethod.GET;
    String value() default "/get";
}
