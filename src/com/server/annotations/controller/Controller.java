package src.com.server.annotations.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller is an annotation used to mark classes that serve as controllers in a web application.
 * It can be used to identify and process controller classes at runtime.
 * The value attribute can be used to specify a base path for the controller.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value();
}
