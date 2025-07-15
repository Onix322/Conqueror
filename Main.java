import configuration.Configuration;
import configuration.ConfigurationImpl;
import build_tool.Loader;

/**
 * Main class to start the application.
 * It initializes the server configuration and starts the application loader.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        //* Server Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        //* Start App
        Loader.load(configuration);

    }

}
