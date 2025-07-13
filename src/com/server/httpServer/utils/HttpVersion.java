package src.com.server.httpServer.utils;

import src.com.server.exceptions.NoCompatibleHttpVersionFound;

/**
 * Enum representing the HTTP versions supported by the server.
 * Currently, only HTTP/1.1 is supported.
 * This enum provides a method to convert a string representation of an HTTP version
 * to its corresponding enum value.
 */
public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion fromString(String text) {
        for (HttpVersion b : HttpVersion.values()) {
            if (b.version.equalsIgnoreCase(text)) {
                return b;
            }
        }

        throw new NoCompatibleHttpVersionFound();
    }

    public String getVersion() {
        return version;
    }
}
