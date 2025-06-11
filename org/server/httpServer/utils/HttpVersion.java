package org.server.httpServer.utils;

import org.server.exceptions.NoCompatibleHttpVersionFound;

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
