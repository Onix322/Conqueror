package src.com.server.httpServer;

import configuration.Configuration;

public sealed interface HttpServer permits HttpServerImpl {

    void start();

    void stop();

    Configuration getConfig();
}
