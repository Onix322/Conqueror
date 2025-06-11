package org.server.httpServer.utils.request.httpRequestStartLine;

import org.server.httpServer.utils.HttpMethod;
import org.server.httpServer.utils.HttpVersion;
import org.server.exceptions.HttpStartLineIncorrect;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class HttpRequestStartLineFactory {
    public static HttpRequestStartLine create(String string) {
        try {
            List<String> lines = Arrays.stream(string.trim().split("\\s+")).toList();

            HttpMethod method = HttpMethod.valueOf(lines.getFirst());
            URI path = new URI(lines.get(1));
            HttpVersion httpVersion = HttpVersion.fromString(lines.getLast());

            return new HttpRequestStartLine(method, path, httpVersion);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpStartLineIncorrect();
        }
    }
}
