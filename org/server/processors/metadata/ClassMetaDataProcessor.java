package org.server.processors.metadata;

import org.server.httpServer.route.ControllerRoute;
import org.server.processors.components.annotations.Singleton;
import org.server.processors.components.annotations.controller.mapping.Mapping;
import org.server.metadata.ControllerMetaData;
import org.server.metadata.MethodMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Singleton
public final class ClassMetaDataProcessor implements MetaDataProcessor<ControllerMetaData> {

    private final MethodMetaDataProcessor METHOD_PROCESSOR;

    private ClassMetaDataProcessor(MethodMetaDataProcessor methodProcessor){
        this.METHOD_PROCESSOR = methodProcessor;
    }

    @Override
    public <A extends Annotation> ControllerMetaData process(Class<?> clazz, Class<A> annotationType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Annotation annotation = clazz.getAnnotation(annotationType);
        Method valueMethod = annotation.getClass().getDeclaredMethod("value");
        String value = (String) valueMethod.invoke(annotation);
        ControllerRoute controllerRoute = new ControllerRoute(value);
        Map<String, MethodMetaData> methodMetaData = this.METHOD_PROCESSOR.process(clazz, Mapping.class);

        return new ControllerMetaData(controllerRoute, clazz, methodMetaData);
    }
}
