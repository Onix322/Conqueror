package src.com.server.httpServer.utils.request.httpRequestBody;

public class HttpRequestBody{

    private Object body;

    public HttpRequestBody(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public <T> T getBody(Class<T> clazz) {
        return clazz.cast(body);
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequestBody{" +
                "body=" + body +
                '}';
    }
}
