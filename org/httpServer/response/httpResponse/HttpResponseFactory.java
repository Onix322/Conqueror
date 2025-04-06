package org.httpServer.response.httpResponse;

import org.httpServer.response.HttpConnectionType;
import org.httpServer.response.httpResponseHeaders.HttpResponseHeader;
import org.httpServer.response.httpResponseStartLine.HttpResponseStartLine;
import org.httpServer.response.HttpStatus;
import org.httpServer.response.httpResponseBody.HttpResponseBody;

public class HttpResponseFactory {
    public static HttpResponse create(String version, HttpStatus status, String contentType, HttpConnectionType connectionType, Object body){

        HttpResponseStartLine httpStatusLine = new HttpResponseStartLine(version, status);
        HttpResponseBody httpResponseBody = new HttpResponseBody(body);
        HttpResponseHeader httpHeader = new HttpResponseHeader(httpResponseBody.toJson().length(), contentType);

        return new HttpResponse(httpStatusLine, httpHeader,connectionType, httpResponseBody);
    }
}
