package framework.src.server.processors.metadata;

import framework.src.server.exceptions.DuplicateMappingMethod;
import framework.src.server.httpServer.utils.httpMethod.HttpMethod;
import framework.src.server.httpServer.utils.route.MethodRoute;
import framework.src.server.metadata.MethodMetaData;
import framework.src.server.annotations.component.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MethodMetaDataProcessor is responsible for processing metadata of methods in a class.
 * It extracts method annotations, parameters, return types, and HTTP methods,
 * and maps them to their respective method metadata.
 */
@Component
public final class MethodMetaDataProcessor implements MetaDataProcessor<Map<String, MethodMetaData>> {

    private MethodMetaDataProcessor() {
    }

    /**
     * Processes the methods of a given class to extract metadata based on the specified annotation type.
     *
     * @param clazz The class whose methods are to be processed.
     * @param extensionOf The annotation type to filter methods by.
     * @return A map of method names to their corresponding MethodMetaData.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is illegal.
     * @throws NoSuchMethodException If the method does not exist.
     */
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

            Parameter[] parameters = method.getParameters();
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

    /**
     * Retrieves the value of a specified method from an annotation.
     *
     * @param annotation The annotation from which to retrieve the value.
     * @param methodName The name of the method to invoke on the annotation.
     * @param methodReturnType The expected return type of the method.
     * @return The value returned by the invoked method, cast to the specified type.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is illegal.
     * @throws NoSuchMethodException If the method does not exist.
     */
    private <T> T getAnnotationValue(Annotation annotation, String methodName, Class<T> methodReturnType) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method gotMethod = annotation.annotationType().getDeclaredMethod(methodName);
        Object value = gotMethod.invoke(annotation);

        return methodReturnType.cast(value);
    }
}
