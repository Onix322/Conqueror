package framework.src.server.httpServer.utils.request.httpRequestBody;

/**
 * HttpRequestBody is a utility class that encapsulates the body of an HTTP request.
 * It allows for easy access and manipulation of the request body, which can be of any type.
 */
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
