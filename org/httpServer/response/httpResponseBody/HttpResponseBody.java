package org.httpServer.response.httpResponseBody;

import org.json.parser.JsonServiceImpl;

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

    public String toJson(){
        return JsonServiceImpl.getInstance().generate(this);
    }

    @Override
    public String toString() {
        return "HttpBody{" +
                "body=" + body +
                '}';
    }
}
