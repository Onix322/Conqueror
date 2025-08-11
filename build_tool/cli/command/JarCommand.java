package build_tool.cli.command;

import configuration.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JarCommand implements Command<Boolean> {

    private final Configuration configuration;

    public JarCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    public static class Holder {
        public static JarCommand INSTANCE = null;
    }

    public static void init(Configuration configuration) {
        if (JarCommand.Holder.INSTANCE == null) {
            JarCommand.Holder.INSTANCE = new JarCommand(configuration);
        }
    }

    public static JarCommand getInstance() {
        return JarCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object... args) {
        String appEntry = this.configuration.readProperty("app.entry");
        String outputAppLocation = this.configuration.readProperty("output.app.location");
        String outputJarsLocation = this.configuration.readProperty("output.jars.location");
        Path outputAppLocationPath = Paths.get(outputAppLocation)
                .normalize();
        Path outputJarsLocationPath = Paths.get(outputJarsLocation)
                .normalize();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "jar",
                "--create",
                "--file", outputJarsLocationPath + "/app.jar",
                "--main-class", appEntry,
                "-C", outputAppLocationPath.toString(), "."
        );

        try {
            Process process = processBuilder.start();
            process.waitFor();
            process.descendants().forEach(ProcessHandle::destroy);
            process.destroy();
            return CommandResult.<Boolean>builder()
                    .setResult(true)
                    .build();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
