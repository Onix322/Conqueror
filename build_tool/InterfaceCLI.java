package build_tool;

import build_tool.utilities.Loader;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class InterfaceCLI {

    private final Configuration configuration;
    private final Loader loader;

    private InterfaceCLI(Configuration configuration, Loader loader) {
        this.configuration = configuration;
        this.loader = loader;
    }

    private static class Holder {
        private static InterfaceCLI INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration, Loader loader) {
        if (InterfaceCLI.Holder.INSTANCE == null) {
            InterfaceCLI.Holder.INSTANCE = new InterfaceCLI(configuration, loader);
        }
    }

    public static InterfaceCLI getInstance() {
        if (InterfaceCLI.Holder.INSTANCE == null) {
            throw new IllegalStateException("InterfaceCLI is not initialized. Use InterfaceCLI.init().");
        }
        return InterfaceCLI.Holder.INSTANCE;
    }

    public void open() throws Exception {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        this.loader.load();
        Optional<ProcessHandle> handle = Optional.empty();
        while (running) {
            System.out.println("[conqueror] -> Type 'help' to see the available commands.");
            System.out.print("[conqueror] -> ");
            String command = scanner.nextLine().trim();
            switch (command) {
                case "init" -> this.loader.load();
                case "build" -> this.buildApp();
                case "start" -> {
                    if (handle.isEmpty()) {
                        long pid = this.startApp().pid();
                        handle = ProcessHandle.of(pid);
                    } else if (!handle.get().isAlive()) {
                        long pid = this.startApp().pid();
                        handle = ProcessHandle.of(pid);
                    } else {
                        System.out.println("App already running...");
                    }
                }
                case "run" -> {
                    this.loader.load();
                    this.buildApp();
                    long pid = this.startApp().pid();
                    handle = ProcessHandle.of(pid);
                }
                case "stop" -> {
                    if (handle.isPresent()) {
                        System.out.println("App is stopping...");
                        handle.get().destroy();
                        handle.get().descendants().forEach(ProcessHandle::destroy);
                        continue;
                    }
                    System.out.println("App is not running...");
                }
                case "status" -> {
                    if (handle.isPresent()) {
                        System.out.println("Status: Running");
                        System.out.println("Parent:" + handle.get() + " -> " + handle.get().isAlive());
                        handle.get()
                                .descendants()
                                .forEach(c -> System.out.println("Child:" + c + " -> " + c.isAlive()));
                        continue;
                    }
                    System.out.println("Status: Shutdown");
                }
                case "help" -> System.out.println("""
                        Available commands:
                        - init           -> Initialize dependencies
                        - build          -> Build framework and user app
                        - start          -> Start the user app
                        - stop           -> Stop the app
                        - run            -> Init, Build and Start
                        - status         -> Check if app status
                        - quit           -> exit build tool
                        """);
                case "quit" -> {
                    if (handle.isPresent()) {
                        System.out.println("App is stopping...");
                        handle.get().destroy();
                        handle.get().descendants().forEach(ProcessHandle::destroy);
                    }
                    scanner.close();
                    running = false;
                    System.out.println("Have a nice day!");
                }
                default -> System.out.println("This command does not exists!");
            }
        }

        if (handle.isPresent()) {
            System.out.println(handle);
        }
    }

    private Process startApp() throws Exception {
        System.out.println("Starting app...");
        String appName = configuration.readProperty("app.name");
        Path outputAppLocation = Path.of(configuration.readProperty("output.app.location"));
        Path appEntry = Path.of(configuration.readProperty("app.entry"));

        List<String> command = new ArrayList<>();
        command.add("cmd.exe");
        command.add("/c"); // /c: execută și închide, dar `start` va face fereastră nouă
        command.add("start");
        command.add('"' + appName + '"');
        command.add("cmd.exe");
        command.add("/k");
        command.add("java -cp " + outputAppLocation + " " + appEntry);

        ProcessBuilder pb = new ProcessBuilder(command);
        return pb.start();
    }

    private void buildApp() throws IOException, InterruptedException {
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
    }

    private List<String> collectJavaSources(Path sourceDir) throws IOException {
        List<String> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(sourceDir)
                .filter(p -> p.toString().endsWith(".java"))) {
            paths.forEach(p -> files.add(p.toString()));
            return files;
        }
    }
}
