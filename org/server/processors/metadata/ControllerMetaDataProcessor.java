package org.server.processors.metadata;

import org.server.httpServer.utils.route.ControllerRoute;
import org.server.annotations.component.Component;
import org.server.annotations.component.controller.mapping.Mapping;
import org.server.metadata.ControllerMetaData;
import org.server.metadata.MethodMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Component
public final class ControllerMetaDataProcessor implements MetaDataProcessor<ControllerMetaData> {

    private final MethodMetaDataProcessor METHOD_PROCESSOR;

    private ControllerMetaDataProcessor(MethodMetaDataProcessor methodProcessor){
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
