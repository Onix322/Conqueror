package build_tool.cli.process;

import configuration.Configuration;

import java.nio.file.Path;
import java.util.List;

public class JavaProcessManager {

    private final Configuration configuration;

    private JavaProcessManager(Configuration configuration) {
        if (!this.isSystemPropertyPresent("java.home")) {
            throw new IllegalStateException("System property 'java.home' is not set!");
        }

        this.configuration = configuration;
    }

    public static class Holder {
        public static JavaProcessManager INSTANCE = null;
    }

    public synchronized static void init(Configuration configuration) {
        if (JavaProcessManager.Holder.INSTANCE == null) {
            JavaProcessManager.Holder.INSTANCE = new JavaProcessManager(configuration);
        }
    }

    public static JavaProcessManager getInstance() {
        return JavaProcessManager.Holder.INSTANCE;
    }

    public ProcessBuilder requestJavaProcess(List<String> commands) {
        Path javaPath = this.requestJavaHome()
                .resolve("java");
        commands.addFirst(javaPath.toString());
        return new ProcessBuilder(commands);
    }

    public ProcessBuilder requestJarProcess(List<String> commands) {
        Path jarPath = this.requestJavaHome()
                .resolve("jar");
        commands.addFirst(jarPath.toString());
        return new ProcessBuilder(commands);
    }

    public ProcessBuilder requestJavacProcess(List<String> commands) {
        Path javacPath = this.requestJavaHome()
                .resolve("javac");
        commands.addFirst(javacPath.toString());
        System.out.println(commands);
        return new ProcessBuilder(commands);
    }

    private Path requestJavaHome() {
        String javaHome = this.configuration.readProperty("java.home");
        Path javaPath = javaHome.equalsIgnoreCase("system") ?
                Path.of(System.getProperty("java.home")) :
                Path.of(javaHome);
        return javaPath.resolve("bin")
                .normalize();
    }

    public boolean isSystemPropertyPresent(String key) {

        StringBuilder errMessage = new StringBuilder()
                .append("System property: ").append(key)
                .append(" is required when running Java process")
                .append(System.lineSeparator())
                .append("Please set System property: ").append(key);

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException(errMessage.toString());
        }
        return System.getProperties().containsKey(key);
    }
}
