package org.server.httpServer.utils.response.httpResponse;

import org.server.httpServer.utils.response.HttpConnectionType;
import org.server.httpServer.utils.response.httpResponseHeaders.HttpResponseHeader;
import org.server.httpServer.utils.response.httpResponseStartLine.HttpResponseStartLine;
import org.server.httpServer.utils.response.HttpStatus;
import org.server.httpServer.utils.response.httpResponseBody.HttpResponseBody;

public class HttpResponseFactory {
    public static HttpResponse create(String version, HttpStatus status, String contentType, HttpConnectionType connectionType, Object body){

        HttpResponseStartLine httpStatusLine = new HttpResponseStartLine(version, status);
        HttpResponseBody httpResponseBody = new HttpResponseBody(body);
        HttpResponseHeader httpHeader = new HttpResponseHeader(httpResponseBody.toString().length(), contentType);

        return new HttpResponse(httpStatusLine, httpHeader,connectionType, httpResponseBody);
    }
}
