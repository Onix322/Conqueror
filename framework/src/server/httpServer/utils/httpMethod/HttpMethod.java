package framework.src.server.httpServer.utils.httpMethod;

/**
 * Enum representing HTTP methods and their body requirements.
 * Each HTTP method is associated with a specific body requirement,
 * indicating whether a request with that method should include a body.
 */
public enum HttpMethod {
    GET(BodyRequirement.FORBIDDEN),
    POST(BodyRequirement.REQUIRED),
    PUT(BodyRequirement.OPTIONAL),
    PATCH(BodyRequirement.REQUIRED),
    DELETE(BodyRequirement.FORBIDDEN),
    HEAD(BodyRequirement.FORBIDDEN),
    OPTIONS(BodyRequirement.OPTIONAL);

    private final BodyRequirement REQUIRES_BODY;

    HttpMethod(BodyRequirement requiresBody) {
        this.REQUIRES_BODY = requiresBody;
    }

    public BodyRequirement hasBody() {
        return REQUIRES_BODY;
    }
}
