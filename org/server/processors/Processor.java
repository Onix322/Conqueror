package org.server.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public interface Processor<R> {
    <A extends Annotation> R process(Class<?> clazz, Class<A> annotationType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
