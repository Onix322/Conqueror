package build_tool.cli.command;

import configuration.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StartCommand implements Command<Process> {

    private final Configuration configuration;

    public StartCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    public static class Holder {
        public static StartCommand INSTANCE = null;
    }

    public static void init(Configuration configuration) {
        if (StartCommand.Holder.INSTANCE == null) {
            StartCommand.Holder.INSTANCE = new StartCommand(configuration);
        }
    }

    public static StartCommand getInstance() {
        return StartCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Process> exec(Object... args) {
        System.out.println("Starting app...");
        List<String> command = this.osCompatibleCommand(System.getProperty("os.name"));
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            return CommandResult.<Process>builder()
                    .setResult(pb.start())
                    .setCommandType("start")
                    .build();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<String> osCompatibleCommand(String os){
        String appName = configuration.readProperty("app.name");
        Path outputAppLocation = Path.of(configuration.readProperty("output.app.location"));
        Path appEntry = Path.of(configuration.readProperty("app.entry"));

        List<String> command = new ArrayList<>();
        if (os.toLowerCase().contains("win")) {
            command.add("cmd.exe");
            command.add("/c");
            command.add("start");
            command.add('"' + appName + '"');
            command.add("cmd.exe");
            command.add("/k");
        } else {
            command.add("sh");
            command.add("-c");
        }

        command.add("java -cp " + outputAppLocation + " " + appEntry);
        return command;
    }
}
