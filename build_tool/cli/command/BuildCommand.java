package build_tool.cli.command;

import build_tool.cli.process.JavaProcessManager;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildCommand implements Command<Process> {

    private final Path outputAppPath;
    private final Path sourcePath;
    private final Path depsPath;
    private final Path configPath;
    private final String jdkVersion;
    private final JavaProcessManager javaProcessManager;

    public BuildCommand(Configuration configuration, JavaProcessManager javaProcessManager) {
        this.outputAppPath = Path.of(configuration.readProperty("output.app.location"))
                .normalize();
        this.sourcePath = Path.of(configuration.readProperty("project.source"))
                .normalize();
        this.depsPath = Path.of(configuration.readProperty("dependencies.location"))
                .normalize();
        this.configPath = Path.of(configuration.readProperty("config.location"))
                .normalize();
        this.jdkVersion = System.getProperty("java.specification.version");
        this.javaProcessManager = javaProcessManager;
    }

    public static class Holder {
        public static BuildCommand INSTANCE = null;
    }

    public synchronized static void init(Configuration configuration, JavaProcessManager javaProcessManager) {
        if (BuildCommand.Holder.INSTANCE == null) {
            BuildCommand.Holder.INSTANCE = new BuildCommand(configuration, javaProcessManager);
        }
    }

    public static BuildCommand getInstance() {
        return BuildCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Process> exec(Object... args) {

        this.deleteFile();
        this.copyConfigFile();
        this.copyDependencies();

        try {
            List<String> commands = this.createCommand();
            ProcessBuilder pb = this.javaProcessManager.requestJavacProcess(commands);

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

        List<String> sources = new LinkedList<>();
        sources.addAll(collectJavaSources(Paths.get("./configuration").normalize()));
        sources.addAll(collectJavaSources(Paths.get(sourcePath.toString())));

        List<String> command = new ArrayList<>();

        String cp = sourcePath.toString()
                + File.pathSeparator + depsPath.toString() + File.separator + "*"
                + File.pathSeparator + depsPath.toString() + File.separator + "*";

        command.add("-cp");
        command.add(cp);

        command.add("--release");
        command.add(jdkVersion);
        command.add("-d");
        command.add(outputAppPath.toString());
        command.addAll(sources);

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

        if(!Files.exists(from)){
            try {
                Files.createDirectories(from);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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


    public void deleteFile(){

        if(!Files.exists(outputAppPath)){
            return;
        }

        try{

            List<Path> paths = Files.walk(outputAppPath)
                    .collect(Collectors.toCollection(ArrayList::new))
                    .reversed();

            for (Path path : paths){
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

