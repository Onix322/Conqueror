package src.com.server.metadata;

import src.com.server.httpServer.utils.httpMethod.HttpMethod;
import src.com.server.httpServer.utils.route.MethodRoute;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * MethodMetaData is a class that encapsulates metadata about an HTTP method.
 * It includes the method's route, name, parameters, return type, and HTTP method type.
 * This class implements the MetaData interface to provide a structured way to access
 * and manipulate method metadata.
 */
public class MethodMetaData implements MetaData<MethodMetaData> {
    private MethodRoute path;
    private String name;
    private Parameter[] parameters;
    private Class<?> returnType;
    private HttpMethod httpMethod;

    public MethodMetaData(MethodRoute path, String name, Parameter[] parameters, Class<?> returnType, HttpMethod httpMethod) {
        this.path = path;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.httpMethod = httpMethod;
    }

    public MethodRoute getPath() {
        return path;
    }

    public void setPath(MethodRoute path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Class<?>[] getParametersClasses() {
        return Arrays.stream(parameters)
                .map(Parameter::getType)
                .toArray(Class<?>[]::new);
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "MappingMethod{" +
                "MethodRoute='" + path+ '\'' +
                ", name='" + name + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", returnType=" + returnType +
                ", httpMethod=" + httpMethod +
                '}';
    }

    @Override
    public MethodMetaData getMetaData() {
        return this;
    }
}
