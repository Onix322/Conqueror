package org.utils.httpServer.request.httpRequestBody;

public class HttpRequestBody{

    private Object body;

    public HttpRequestBody(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
