import org.httpServer.HttpServer;
import org.httpServer.HttpServerImpl;
import utils.configuration.Configuration;
import utils.configuration.ConfigurationImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Conqueror {

    public static void main(String[] args) {
        System.out.println("Starting app...");

        //HTTP SERVER Configuration
        Configuration configuration = ConfigurationImpl.getInstance();

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        HttpServerImpl.HttpServerInit.create(configuration, executorService);

        HttpServer httpServer = HttpServerImpl.HttpServerInit.getInstance();

        httpServer.start();
    }
}
