package framework.src.server.httpServer.utils.request.httpRequestHeader;

/**
 * Represents an HTTP request header with a key and a value.
 * This class is used to encapsulate the key-value pairs of HTTP headers
 * that are sent in an HTTP request.
 */
public class HttpRequestHeader {
    private String key;
    private Object value;

    public HttpRequestHeader(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
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
