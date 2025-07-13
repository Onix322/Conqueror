package src.com.server.httpServer.utils.response.httpResponseBody;

/**
 * Represents the body of an HTTP response.
 * This class encapsulates the body content that will be sent back to the client
 * in response to an HTTP request.
 */
public class HttpResponseBody {

    private Object body;

    public HttpResponseBody(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpBody{" +
                "body=" + body +
                '}';
    }
}
