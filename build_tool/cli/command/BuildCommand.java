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

    private final Path outputAppPath;
    private final Path sourcePath;
    private final Path depsPath;
    private final Path configPath;

    public BuildCommand(Configuration configuration) {
        this.outputAppPath = Path.of(configuration.readProperty("output.app.location"))
                .normalize();
        this.sourcePath = Path.of(configuration.readProperty("project.source"))
                .normalize();
        this.depsPath = Path.of(configuration.readProperty("dependencies.location"))
                .normalize();
        this.configPath = Path.of(configuration.readProperty("config.location"))
                .normalize();
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

        List<String> command = this.createCommand();
        this.copyConfigFile();
        this.copyDependencies();

        try {
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

    private List<String> createCommand() {
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("--release");
        command.add("21");
        command.add("-d");
        command.add(outputAppPath.toString());
        command.addAll(collectJavaSources(Paths.get(sourcePath.toString())));

        return command;
    }

    private synchronized void copyDependencies() {
        this.copy(depsPath, outputAppPath.resolve("classes"));
    }

    public void copyConfigFile() {

        File outputAppFile = outputAppPath.toFile();
        Path appOutputConfig = Paths.get(outputAppPath.toString(), configPath.toString())
                .normalize();

        if (!outputAppFile.exists()) {
            outputAppFile.mkdirs();
        }

        appOutputConfig.toFile().mkdirs();
        appOutputConfig.toFile().setWritable(true);

        try {
            Files.copy(
                    configPath,
                    appOutputConfig,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copy(Path from, Path to) {
        Path bootOut = to.normalize();
        File bootFile = bootOut.toFile();

        if (!bootFile.exists()) {
            bootFile.mkdir();
        }

        try (Stream<Path> paths = Files.walk(from)) {
            for (Path p : paths.toList()) {
                if (p.equals(from)) continue;
                Path source = from.relativize(p.normalize());
                Path target = bootOut.resolve(source);
                Files.copy(p, target.normalize(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

