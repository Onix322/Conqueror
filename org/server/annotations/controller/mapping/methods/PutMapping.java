package org.server.annotations.controller.mapping.methods;

import org.server.annotations.controller.mapping.Mapping;
import org.server.httpServer.utils.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PutMapping {
    HttpMethod httpMethod() default HttpMethod.DELETE;
    String value() default "/put";
}
