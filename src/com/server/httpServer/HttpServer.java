package src.com.server.httpServer;

import src.com.server.configuration.Configuration;

public sealed interface HttpServer permits HttpServerImpl {

    void start();

    void stop();

    Configuration getConfig();
}
