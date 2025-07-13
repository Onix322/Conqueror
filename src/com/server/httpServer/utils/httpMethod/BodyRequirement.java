package src.com.server.httpServer.utils.httpMethod;

/**
 * Enum representing the requirements for the body of an HTTP request.
 * This is used to indicate whether a body is required, optional, or forbidden
 * for a specific HTTP method.
 */
public enum BodyRequirement {
    REQUIRED,
    OPTIONAL,
    FORBIDDEN
}