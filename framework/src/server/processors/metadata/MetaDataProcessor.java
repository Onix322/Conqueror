package framework.src.server.processors.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 * MetaDataProcessor is a generic interface for processing metadata annotations.
 * It defines a method to process a class with a specified annotation type and return a result of type R.
 *
 * @param <R> the type of the result produced by the processing
 */
public sealed interface MetaDataProcessor<R> permits ControllerMetaDataProcessor, MethodMetaDataProcessor{
    <A extends Annotation> R process(Class<?> clazz, Class<A> annotationType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
