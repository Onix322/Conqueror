package org.httpServer.HttpResponse.response;

public class HttpBody<T> {

    private T body;

    public HttpBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getBodyString(){
        return " CREATE JSON FACTORY ";
    }

    @Override
    public String toString() {
        return "HttpBody{" +
                "body=" + body +
                '}';
    }
}
