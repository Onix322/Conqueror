package org.server.httpServer.utils.httpMethod;

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
