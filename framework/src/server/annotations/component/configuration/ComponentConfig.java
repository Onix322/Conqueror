package framework.src.server.annotations.component.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/*
 * ComponentConfig is an annotation used to mark classes that are part of the component configuration.
 * It can be used to identify and process component configurations at runtime.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentConfig {
}
