package build_tool.cli.command;

import build_tool.cli.process.JavaProcessManager;
import configuration.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JarCommand implements Command<Boolean> {

    private final Configuration configuration;
    private  final JavaProcessManager javaProcessManager;

    public JarCommand(Configuration configuration, JavaProcessManager javaProcessManager) {
        this.configuration = configuration;
        this.javaProcessManager = javaProcessManager;
    }

    public static class Holder {
        public static JarCommand INSTANCE = null;
    }

    public synchronized static void init(Configuration configuration, JavaProcessManager javaProcessManager) {
        if (JarCommand.Holder.INSTANCE == null) {
            JarCommand.Holder.INSTANCE = new JarCommand(configuration, javaProcessManager);
        }
    }

    public static JarCommand getInstance() {
        return JarCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object... args) {

        String outputJarsLocation = this.configuration.readProperty("output.jars.location");
        Path outputJarsLocationPath = Paths.get(outputJarsLocation)
                .normalize();

        List<String> command = this.createCommand(outputJarsLocationPath);
        ProcessBuilder processBuilder = this.javaProcessManager.requestJarProcess(command);
        System.out.println(processBuilder.command());

        try {
            System.out.println("Starting jar creation process...");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Process finished successfully!");
            }

            if (Files.exists(outputJarsLocationPath.resolve("app.jar"))) {
                System.out.println("Jar created: " + Files.exists(outputJarsLocationPath.resolve("app.jar")));
                System.out.println("Jar path: " + outputJarsLocationPath.resolve("app.jar"));
            } else {
                System.err.println("Jar created: false");
            }

            process.descendants().forEach(ProcessHandle::destroy);
            process.destroy();
            return CommandResult.<Boolean>builder()
                    .setResult(true)
                    .build();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> createCommand(Path outputJarsLocationPath){
        String appEntry = this.configuration.readProperty("app.entry");
        String outputAppLocation = this.configuration.readProperty("output.app.location");
        Path outputAppLocationPath = Paths.get(outputAppLocation)
                .normalize();

        List<String> command = new ArrayList<>();
        command.add("--create");
        command.add("--file");
        command.add(outputJarsLocationPath.resolve("app.jar").normalize().toString());
        command.add("--main-class");
        command.add(appEntry);
        command.add("-C");
        command.add(outputAppLocationPath.toString());
        command.add(".");

        return command;
    }

}
