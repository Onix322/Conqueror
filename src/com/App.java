package src.com;

import jakarta.persistence.Entity;
import src.com.server.annotations.component.Component;
import src.com.server.annotations.component.configuration.ComponentConfig;
import src.com.server.annotations.controller.Controller;
import configuration.Configuration;
import src.com.server.httpServer.HttpServer;
import src.com.server.processors.context.ApplicationContext;

import java.util.concurrent.ExecutorService;

public class App {

    public static void start(Configuration configuration, ExecutorService executorService) throws Exception {
        System.out.println("Starting app...");

        //* ApplicationContext Initialization
        ApplicationContext applicationContext = new ApplicationContext(configuration, executorService);
        applicationContext.registerAnnotation(Entity.class);
        applicationContext.registerAnnotation(ComponentConfig.class);
        applicationContext.registerAnnotation(Component.class);
        applicationContext.registerAnnotation(Controller.class);
        applicationContext.applicationContextInit();

        //* Server start
        applicationContext.requestInstance(HttpServer.class)
                .start();
    }
}
