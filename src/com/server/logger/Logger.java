package src.com.server.logger;


public class Logger {

    public static void log(Class<?> clazz, String message) {
        System.out.println('[' + clazz.getSimpleName() + "] -> " + message);
    }
}
