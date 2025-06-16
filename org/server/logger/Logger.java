package org.server.logger;

import org.server.configuration.Configuration;
import org.server.environment.Environment;



public class Logger {

    public static void log(Class<?> clazz, String message) {
        System.out.println('[' + clazz.getSimpleName() + "] -> " + message);
    }
}
