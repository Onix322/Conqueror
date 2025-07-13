package src.com.server.httpServer.utils.route;

/**
 * Represents a path variable in an HTTP route.
 * This class is used to encapsulate the name and value of a path variable
 * that can be extracted from the URL of an HTTP request.
 */
public record PathVariable(String name, Object value) {
}
