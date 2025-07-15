package framework.src.server.httpServer;

import configuration.Configuration;

/**
 * Represents an HTTP server interface.
 * This interface defines the basic operations for starting and stopping
 * an HTTP server, as well as retrieving its configuration.
 */
public sealed interface HttpServer permits HttpServerImpl {

    void start();

    void stop();

    Configuration getConfig();
}
