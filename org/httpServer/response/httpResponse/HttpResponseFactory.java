package org.httpServer.response.httpResponse;

import org.httpServer.response.httpHeaders.HttpHeaders;
import org.httpServer.response.httpStartLine.HttpStartLine;
import org.httpServer.response.HttpStatus;
import org.httpServer.response.httpBody.HttpBody;

public class HttpResponseFactory {
    public static <T> HttpResponse<T> create(String version, HttpStatus status, String contentType, T body){

        HttpStartLine httpStatusLine = new HttpStartLine(version, status);
        HttpBody<T> httpBody = new HttpBody<>(body);
        HttpHeaders httpHeader = new HttpHeaders(httpBody.toJson().length(), contentType);

        return new HttpResponse<>(httpStatusLine, httpHeader, httpBody);
    }
}
