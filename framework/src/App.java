package framework.src;

import configuration.Configuration;
import framework.src.server.annotations.component.Component;
import framework.src.server.annotations.component.ComponentEntity;
import framework.src.server.annotations.component.configuration.ComponentConfig;
import framework.src.server.annotations.controller.Controller;
import framework.src.server.httpServer.HttpServer;
import framework.src.server.processors.context.ApplicationContext;

import java.util.concurrent.ExecutorService;

/**
 * The main application class that initializes the application context and starts the HTTP server.
 * <p>
 * To add annotations, use the following:
 * applicationContext.registerAnnotation(YourAnnotation.class);
 * </p>
 */
public class App {

    /**
     * The main method to start the application.
     *
     * @param configuration   the configuration for the application
     * @param executorService the executor service for handling asynchronous tasks
     * @throws Exception if an error occurs during application startup
     */
    public static void start(Configuration configuration, ExecutorService executorService) throws Exception {
        System.out.println("Starting app...");

        //* ApplicationContext Initialization
        ApplicationContext applicationContext = new ApplicationContext(configuration, executorService);
        applicationContext.registerAnnotation(ComponentConfig.class);
        applicationContext.registerAnnotation(Component.class);
        applicationContext.registerAnnotation(Controller.class);
        applicationContext.registerAnnotation(ComponentEntity.class);
        applicationContext.applicationContextInit();

        //* Server start
        applicationContext.requestInstance(HttpServer.class)
                .start();
    }
}
