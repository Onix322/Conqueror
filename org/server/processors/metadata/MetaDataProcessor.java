package org.server.processors.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public interface MetaDataProcessor<R> {
    <A extends Annotation> R process(Class<?> clazz, Class<A> annotationType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
