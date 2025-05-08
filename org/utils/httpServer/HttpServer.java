package org.utils.httpServer;

import org.utils.configuration.Configuration;

public interface HttpServer {

    void start();

    void stop();

    Configuration getConfig();
}
