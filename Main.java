import build_tool.cli.InterfaceCLI;
import build_tool.cli.command.*;
import build_tool.utilities.Loader;
import configuration.Configuration;
import configuration.ConfigurationImpl;

/**
 * Main class to start the application.
 * It initializes the server configuration and starts the application.
 */
public class Main {

    public static void main(String[] args) {

        //* Setup Configuration
        ConfigurationImpl.init();
        Configuration configuration = ConfigurationImpl.getInstance();

        Loader.init(configuration);
        Loader loader = Loader.getInstance();

        //* Start App
        //Commands
        InitCommand.init(loader);
        InitCommand initCommand = InitCommand.getInstance();

        BuildCommand.init(configuration);
        BuildCommand buildCommand = BuildCommand.getInstance();

        StartCommand.init(configuration);
        StartCommand startCommand = StartCommand.getInstance();

        StopCommand.init();
        StopCommand stopCommand = StopCommand.getInstance();

        StatusCommand.init();
        StatusCommand statusCommand = StatusCommand.getInstance();

        HelpCommand.init();
        HelpCommand helpCommand = HelpCommand.getInstance();

        QuitCommand.init(stopCommand);
        QuitCommand quitCommand = QuitCommand.getInstance();

        NoCommand.init();
        NoCommand noCommand = NoCommand.getInstance();

        RunCommand.init(initCommand, startCommand, buildCommand);
        RunCommand runCommand = RunCommand.getInstance();

        JarCommand.init(configuration);
        JarCommand jarCommand = JarCommand.getInstance();

        //CommandRegistry
        CommandRegistry.init(noCommand);
        CommandRegistry commandRegistry = CommandRegistry.getInstance();
        commandRegistry.registerCommand("init", () -> initCommand);
        commandRegistry.registerCommand("build", () -> buildCommand);
        commandRegistry.registerCommand("start", () -> startCommand);
        commandRegistry.registerCommand("stop", () -> stopCommand);
        commandRegistry.registerCommand("status", () -> statusCommand);
        commandRegistry.registerCommand("help", () -> helpCommand);
        commandRegistry.registerCommand("run", () -> runCommand);
        commandRegistry.registerCommand("jar", () -> jarCommand);
        commandRegistry.registerCommand("quit", () -> quitCommand);

        InterfaceCLI.init(commandRegistry);
        InterfaceCLI interfaceCLI = InterfaceCLI.getInstance();

        interfaceCLI.start();
    }
}
