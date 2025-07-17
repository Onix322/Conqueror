import build_tool.InterfaceCLI;
import build_tool.utilities.Loader;
import configuration.Configuration;
import configuration.ConfigurationImpl;

/**
 * Main class to start the application.
 * It initializes the server configuration and starts the application.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        //* Setup Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        Loader.init(configuration);
        Loader loader = Loader.getInstance();

        //* Start App
        InterfaceCLI.init(configuration, loader);
        InterfaceCLI interfaceCLI = InterfaceCLI.getInstance();

        interfaceCLI.open();
    }
}
