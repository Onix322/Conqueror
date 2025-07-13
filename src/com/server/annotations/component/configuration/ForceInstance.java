package src.com.server.annotations.component.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ForceInstance is an annotation used to indicate that a method should always
 * create a new instance of the component, even if an instance already exists.
 * This is typically used in dependency injection scenarios where a fresh instance
 * is required for each invocation.
 * * Example usage:
 * <pre>
 *     {@code
 *     @ForceInstance
 *     public MyComponent createMyComponent() {
 *     return new MyComponent();
 *     }
 *     }
 *     *
 * </pre>
 * It will force the instance of a component configured in context and use it in injection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ForceInstance {
}
