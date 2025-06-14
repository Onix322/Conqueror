package org.server.annotations.controller.mapping.methods;

import org.server.annotations.controller.mapping.Mapping;
import org.server.httpServer.utils.httpMethod.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PatchMethod {
    HttpMethod httpMethod() default HttpMethod.PATCH;
    String value() default "/patch";
}
