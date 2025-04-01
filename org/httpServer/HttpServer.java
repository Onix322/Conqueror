package org.httpServer;

import org.configuration.Configuration;

public interface HttpServer {

    void start();

    void stop();

    Configuration getConfig();
}
