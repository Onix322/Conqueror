package org.httpServer.response.httpBody;

import utils.json.JsonParserImpl;

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
