package framework.src.server.processors.metadata;

import framework.src.server.httpServer.utils.route.ControllerRoute;
import framework.src.server.annotations.component.Component;
import framework.src.server.annotations.controller.mapping.Mapping;
import framework.src.server.metadata.ControllerMetaData;
import framework.src.server.metadata.MethodMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * ControllerMetaDataProcessor is responsible for processing metadata of controllers.
 * It extracts the route and method metadata from a controller class annotated with @Controller.
 * This processor is used to gather information about the controller's routes and methods
 * for further processing in the application.
 */
@Component
public final class ControllerMetaDataProcessor implements MetaDataProcessor<ControllerMetaData> {

    private final MethodMetaDataProcessor METHOD_PROCESSOR;

    private ControllerMetaDataProcessor(MethodMetaDataProcessor methodProcessor){
        this.METHOD_PROCESSOR = methodProcessor;
    }

    /**
     * Processes the given class to extract controller metadata.
     *
     * @param clazz The class to process.
     * @param annotationType The type of annotation to look for (e.g., @Controller).
     * @return A ControllerMetaData object containing the route, class, and method metadata.
     * @throws InvocationTargetException If an exception occurs during method invocation.
     * @throws IllegalAccessException If access to the method is illegal.
     * @throws NoSuchMethodException If the method does not exist.
     */
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
