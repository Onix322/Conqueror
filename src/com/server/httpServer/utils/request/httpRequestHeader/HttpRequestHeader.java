package src.com.server.httpServer.utils.request.httpRequestHeader;

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
