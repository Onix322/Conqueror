package framework.src;

import configuration.Configuration;
import configuration.ConfigurationImpl;
import framework.src.boot.Boot;
import framework.src.server.annotations.component.Component;
import framework.src.server.annotations.component.ComponentEntity;
import framework.src.server.annotations.component.configuration.ComponentConfig;
import framework.src.server.annotations.controller.Controller;
import framework.src.server.httpServer.HttpServer;
import framework.src.server.processors.context.ApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The main application class that initializes the application context and starts the HTTP server.
 * <p>
 * To add annotations, use the following:
 * applicationContext.registerAnnotation(YourAnnotation.class);
 * </p>
 */
public class App {

    public static void appInit() throws IOException, ClassNotFoundException {
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

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

    public static void main(String[] args) {
        List<String> argsList = List.of(args);
        try {
            if (argsList.contains("-boot")) {
                Boot.boot("app.jar", "framework", "result/app/framework", false);
            }
            appInit();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
