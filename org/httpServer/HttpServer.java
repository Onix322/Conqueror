package org.httpServer;

import utils.configuration.Configuration;

public interface HttpServer {

    void start();

    void stop();

    Configuration getConfig();
}
