package org.server.processors;

import org.server.httpServer.route.ControllerRoute;
import org.server.processors.annotations.Singleton;
import org.server.processors.annotations.controller.mapping.Mapping;
import org.server.metadata.ControllerMetaData;
import org.server.metadata.MethodMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Singleton
public class ClassProcessor implements Processor<ControllerMetaData> {

    private final MethodProcessor METHOD_PROCESSOR;

    private ClassProcessor(MethodProcessor methodProcessor){
        this.METHOD_PROCESSOR = methodProcessor;
    };

    private static class Init{
        private static ClassProcessor INSTANCE = null;
    }

    public synchronized static void init(MethodProcessor methodProcessor){
        if(Init.INSTANCE == null){
            Init.INSTANCE = new ClassProcessor(methodProcessor);
        }
    }

    public static ClassProcessor getInstance(){
        if(Init.INSTANCE == null){
            throw new IllegalStateException("ClassProcessor not initialized. Use ClassProcessor.init()");
        }
        return Init.INSTANCE;
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

    public MethodProcessor getMethodProcessor() {
        return METHOD_PROCESSOR;
    }
}
