package org.server.processors.metadata;

import org.server.exepltions.DuplicateMappingMethod;
import org.server.httpServer.HttpMethod;
import org.server.httpServer.route.MethodRoute;
import org.server.metadata.MethodMetaData;
import org.server.processors.components.annotations.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public final class MethodMetaDataProcessor implements MetaDataProcessor<Map<String, MethodMetaData>> {

    private MethodMetaDataProcessor() {
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
                String value = this.getAnnotationValue(annotation, "value", String.class);
                MethodRoute methodRoute = new MethodRoute(value);
                HttpMethod httpMethod = this.getAnnotationValue(annotation, "httpMethod", HttpMethod.class);

                MethodMetaData methodMetadata = new MethodMetaData(methodRoute, method.getName(), parameters, returnType, httpMethod);
                if (mappedMethods.containsKey(value)) {
                    throw new DuplicateMappingMethod(clazz + " has duplicated mapping annotation: " + annotation);
                }
                mappedMethods.put(value, methodMetadata);
            }
        }

        return mappedMethods;
    }

    private <T> T getAnnotationValue(Annotation annotation, String methodName, Class<T> methodReturnType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method gotMethod = annotation.annotationType().getDeclaredMethod(methodName);
        Object value = gotMethod.invoke(annotation);

        return methodReturnType.cast(value);
    }
}
