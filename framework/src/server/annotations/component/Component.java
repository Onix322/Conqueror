package framework.src.server.annotations.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Component annotation is used to mark a class as a component in the application.
 * It can be used for dependency injection or component scanning purposes.
 * The name attribute can be used to specify a custom name for the component.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
    String name() default "";
}
