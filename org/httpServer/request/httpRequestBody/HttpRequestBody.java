package org.httpServer.request.httpRequestBody;

public class HttpRequestBody <T>{

    private T body;

    public HttpRequestBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
