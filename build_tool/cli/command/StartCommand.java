package build_tool.cli.command;

import build_tool.cli.process.JavaProcessManager;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StartCommand implements Command<Process> {

    private final Configuration configuration;
    private final JavaProcessManager javaProcessManager;

    public StartCommand(Configuration configuration, JavaProcessManager javaProcessManager) {
        this.configuration = configuration;
        this.javaProcessManager = javaProcessManager;
    }

    public static class Holder {
        public static StartCommand INSTANCE = null;
    }

    public synchronized static void init(Configuration configuration, JavaProcessManager javaProcessManager) {
        if (StartCommand.Holder.INSTANCE == null) {
            StartCommand.Holder.INSTANCE = new StartCommand(configuration, javaProcessManager);
        }
    }

    public static StartCommand getInstance() {
        return StartCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Process> exec(Object... args) {
        System.out.println("Starting app...");

        List<String> command = this.createCommand();
        ProcessBuilder pb = this.javaProcessManager.requestJavaProcess(command);
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

    private List<String> createCommand(){
        Path outputAppLocation = Path.of(configuration.readProperty("output.app.location"));
        String appEntry = configuration.readProperty("app.entry");
        List<String> command = new ArrayList<>();
        command.add("-cp");
        command.add(outputAppLocation.normalize().toString().replace(File.separator, "/"));
        command.add(appEntry);

        return command;
    }
}
