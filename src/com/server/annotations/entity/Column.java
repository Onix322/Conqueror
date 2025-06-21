package src.com.server.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name();
    boolean nullable() default true;
    boolean unique() default false;
    boolean primary() default false;
    boolean autoIncrement() default false;
    boolean idColumn() default false;
}
