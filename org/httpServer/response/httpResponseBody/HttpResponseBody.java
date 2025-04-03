package org.httpServer.response.httpResponseBody;

import utils.json.JsonParserImpl;

public class HttpResponseBody<T> {

    private T body;

    public HttpResponseBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String toJson(){
        return JsonParserImpl.getInstance().generate(this);
    }

    @Override
    public String toString() {
        return "HttpBody{" +
                "body=" + body +
                '}';
    }
}
