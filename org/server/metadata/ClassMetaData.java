package org.server.metadata;

import java.util.Map;

public class ClassMetaData implements MetaData<ClassMetaData> {
    private String path;
    private Class<?> clazz;
    private Map<String, MethodMetaData> mappingMethods;

    public ClassMetaData(String path, Class<?> clazz, Map<String, MethodMetaData> mappingMethods) {
        this.path = path;
        this.clazz = clazz;
        this.mappingMethods = mappingMethods;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
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
    public ClassMetaData getMetaData() {
        return this;
    }
}
