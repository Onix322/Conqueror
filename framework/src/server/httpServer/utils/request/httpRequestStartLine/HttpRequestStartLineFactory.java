package framework.src.server.httpServer.utils.request.httpRequestStartLine;

import framework.src.server.httpServer.utils.httpMethod.HttpMethod;
import framework.src.server.httpServer.utils.HttpVersion;
import framework.src.server.exceptions.HttpStartLineIncorrect;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Factory class for creating HttpRequestStartLine instances from a string representation.
 * The string should contain the HTTP method, path, and HTTP version separated by spaces.
 * Example: "GET /index.html HTTP/1.1"
 */
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
