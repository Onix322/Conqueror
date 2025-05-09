package org.server.httpServer.response.httpResponseBody;

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
