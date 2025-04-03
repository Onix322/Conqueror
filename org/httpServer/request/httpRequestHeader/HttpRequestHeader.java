package org.httpServer.request.httpRequestHeader;

public class HttpRequestHeader<T> {
    private String key;
    private T value;

    public HttpRequestHeader(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HttpRequestHeader{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
