package org.server.processors;

import org.server.exepltions.DuplicateMappingMethod;
import org.server.httpServer.HttpMethod;
import org.server.httpServer.route.MethodRoute;
import org.server.metadata.MetaData;
import org.server.metadata.MethodMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodProcessor implements Processor<Map<String, MethodMetaData>>{

    private MethodProcessor(){};

    private static class Init{
        private static MethodProcessor INSTANCE = null;
    }

    public synchronized static void init(){
        if(Init.INSTANCE == null){
            Init.INSTANCE = new MethodProcessor();
        }
    }

    @SuppressWarnings("unchecked")
    public static MethodProcessor getInstance(){
        if(Init.INSTANCE == null){
            throw new IllegalStateException("ClassProcessor not initialized. Use ClassProcessor.init()");
        }
        return Init.INSTANCE;
    }

    @Override
    public <A extends Annotation> Map<String, MethodMetaData> process(Class<?> clazz, Class<A> extensionOf) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, MethodMetaData> mappedMethods = new LinkedHashMap<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers())) continue;
            Set<Annotation> annotations = Arrays.stream(method.getDeclaredAnnotations())
                    .filter(a -> a.annotationType().isAnnotationPresent(extensionOf))
                    .collect(Collectors.toSet());
            if (annotations.isEmpty()) continue;

            Class<?>[] parameters = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();

            for (Annotation annotation : annotations) {

                Method valueMethod = annotation.annotationType().getDeclaredMethod("value");
                String value = (String) valueMethod.invoke(annotation);
                MethodRoute methodRoute = new MethodRoute(value);
                Method gotHttpMethod = annotation.annotationType().getDeclaredMethod("httpMethod");
                HttpMethod httpMethod = (HttpMethod) gotHttpMethod.invoke(annotation);

                MethodMetaData methodMetadata = new MethodMetaData(methodRoute, method.getName(), parameters, returnType, httpMethod);
                if (mappedMethods.containsKey(value)) {
                    throw new DuplicateMappingMethod(clazz + " has duplicated mapping annotation: " + annotation);
                }
                mappedMethods.put(value, methodMetadata);
            }
        }
        return mappedMethods;
    }
}
