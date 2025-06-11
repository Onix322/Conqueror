package org.server.httpServer;

import org.server.configuration.Configuration;

public sealed interface HttpServer permits HttpServerImpl {

    void start();

    void stop();

    Configuration getConfig();
}
