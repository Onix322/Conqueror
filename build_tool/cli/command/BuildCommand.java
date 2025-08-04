package build_tool.cli.command;

import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BuildCommand implements Command<Process> {

    private final Configuration configuration;

    public BuildCommand(Configuration configuration) {
        this.configuration = configuration;
    }

    public static class Holder {
        public static BuildCommand INSTANCE = null;
    }

    public static void init(Configuration configuration) {
        if (BuildCommand.Holder.INSTANCE == null) {
            BuildCommand.Holder.INSTANCE = new BuildCommand(configuration);
        }
    }

    public static BuildCommand getInstance() {
        return BuildCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Process> exec(Object... args) {
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("--release");
        command.add("21");
        command.add("-d");
        command.add("result/app");
        command.addAll(collectJavaSources(Paths.get("framework/src")));

        Path outputAppLocation = Path.of(configuration.readProperty("output.app.location"));
        File outputAppFile = outputAppLocation.toFile();

        Path configLocation = Path.of(configuration.readProperty("config.location"));
        Path appOutputConfig = Paths.get(outputAppLocation.toString(), configLocation.toString());

        if(!outputAppFile.exists()){
            outputAppFile.mkdirs();
        }

        appOutputConfig.toFile().mkdirs();
        appOutputConfig.toFile().setWritable(true);

        try {
            Files.copy(
                    configLocation.normalize(),
                    appOutputConfig.normalize(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();
            Process process = pb.start();
            if (process.isAlive()) {
                System.out.println("Building app...");
            }
            process.waitFor();
            if (!process.isAlive()) {
                System.out.println("Build done!");
            }

            process.destroy();

            return CommandResult.<Process>builder()
                    .setResult(process)
                    .setCommandType("build")
                    .build();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> collectJavaSources(Path sourceDir) {
        List<String> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(sourceDir)
                .filter(p -> p.toString().endsWith(".java"))) {
            paths.forEach(p -> files.add(p.toString()));
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

