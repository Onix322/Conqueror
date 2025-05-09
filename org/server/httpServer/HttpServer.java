package org.server.httpServer;

import org.server.configuration.Configuration;

public interface HttpServer {

    void start();

    void stop();

    Configuration getConfig();
}
