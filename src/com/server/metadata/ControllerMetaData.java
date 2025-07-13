package src.com.server.metadata;

import src.com.server.httpServer.utils.route.ControllerRoute;

import java.util.Map;

/**
 * ControllerMetaData is a class that holds metadata for a controller in a web application.
 * It includes the route of the controller, the class representing the controller,
 * and a mapping of method names to their corresponding MethodMetaData.
 */
public class ControllerMetaData implements MetaData<ControllerMetaData> {
    private ControllerRoute path;
    private Class<?> clazz;
    private Map<String, MethodMetaData> mappingMethods;

    public ControllerMetaData(ControllerRoute path, Class<?> clazz, Map<String, MethodMetaData> mappingMethods) {
        this.path = path;
        this.clazz = clazz;
        this.mappingMethods = mappingMethods;
    }

    public ControllerRoute getPath() {
        return path;
    }

    public void setPath(ControllerRoute path) {
        this.path = path;
    }

    public Class<?> getClassOf() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getClassOf(Class<T> type) {
        if(clazz.isAssignableFrom(type)){
            return (Class<T>) clazz;
        }
        throw new IllegalStateException(clazz + " is not type of " + type);
    }

    public <T> void setClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Map<String, MethodMetaData> getMethodsMetaData() {
        return mappingMethods;
    }

    public void setMethodsMetaData(Map<String, MethodMetaData> mappingMethods) {
        this.mappingMethods = mappingMethods;
    }

    @Override
    public String toString() {
        return "ControllerTemplate{" +
                "path='" + path + '\'' +
                ", class=" + clazz +
                ", methodsMetaData=" + mappingMethods +
                '}';
    }

    @Override
    public ControllerMetaData getMetaData() {
        return this;
    }
}
