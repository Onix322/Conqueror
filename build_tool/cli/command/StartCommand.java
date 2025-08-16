package build_tool.cli.command;

import configuration.Configuration;

import java.io.File;
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

        Path outputAppLocation = Path.of(configuration.readProperty("output.app.location"));
        String appEntry = configuration.readProperty("app.entry");

        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-cp");
        command.add(outputAppLocation.normalize().toString().replace(File.separator, "/"));
        command.add(appEntry);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            return CommandResult.<Process>builder()
                    .setResult(pb.start())
                    .setCommandType("start")
                    .build();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
