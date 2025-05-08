package org.utils.httpServer.response.httpResponse;

import org.utils.httpServer.response.HttpConnectionType;
import org.utils.httpServer.response.httpResponseHeaders.HttpResponseHeader;
import org.utils.httpServer.response.httpResponseStartLine.HttpResponseStartLine;
import org.utils.httpServer.response.HttpStatus;
import org.utils.httpServer.response.httpResponseBody.HttpResponseBody;

public class HttpResponseFactory {
    public static HttpResponse create(String version, HttpStatus status, String contentType, HttpConnectionType connectionType, Object body){

        HttpResponseStartLine httpStatusLine = new HttpResponseStartLine(version, status);
        HttpResponseBody httpResponseBody = new HttpResponseBody(body.toString());
        HttpResponseHeader httpHeader = new HttpResponseHeader(httpResponseBody.toString().length(), contentType);

        return new HttpResponse(httpStatusLine, httpHeader,connectionType, httpResponseBody);
    }
}
