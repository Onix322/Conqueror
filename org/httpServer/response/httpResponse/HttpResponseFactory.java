package org.httpServer.response.httpResponse;

import org.httpServer.response.httpResponseHeaders.HttpResponseHeader;
import org.httpServer.response.httpResponseStartLine.HttpResponseStartLine;
import org.httpServer.response.HttpStatus;
import org.httpServer.response.httpResponseBody.HttpResponseBody;

public class HttpResponseFactory {
    public static <T> HttpResponse<T> create(String version, HttpStatus status, String contentType, T body){

        HttpResponseStartLine httpStatusLine = new HttpResponseStartLine(version, status);
        HttpResponseBody<T> httpResponseBody = new HttpResponseBody<>(body);
        HttpResponseHeader httpHeader = new HttpResponseHeader(httpResponseBody.toJson().length(), contentType);

        return new HttpResponse<>(httpStatusLine, httpHeader, httpResponseBody);
    }
}
