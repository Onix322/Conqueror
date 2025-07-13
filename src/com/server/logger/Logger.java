package src.com.server.logger;

/**
 * Logger class for logging messages with the class name.
 * This class provides a simple logging mechanism that outputs messages
 * to the console, prefixed with the name of the class from which the log
 * method is called.
 */
public class Logger {

    public static void log(Class<?> clazz, String message) {
        System.out.println('[' + clazz.getSimpleName() + "] -> " + message);
    }
}
