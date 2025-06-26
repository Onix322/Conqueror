package loader.utilities;

import src.com.server.configuration.Configuration;

import java.io.File;
import java.io.IOException;

public class ClassPathLoader {

    private final String dependenciesLocation;
    private final String classpathLocation;
    private final ProcessBuilder processBuilder;

    private ClassPathLoader(Configuration configuration, ProcessBuilder processBuilder) {
        this.dependenciesLocation = configuration.readProperty("dependencies.location");
        this.classpathLocation = configuration.readProperty("classpath.location");
        this.processBuilder = processBuilder;
    }

    private static class Holder {
        private static ClassPathLoader INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration, ProcessBuilder processBuilder) {
        if (ClassPathLoader.Holder.INSTANCE == null) {
            ClassPathLoader.Holder.INSTANCE = new ClassPathLoader(configuration, processBuilder);
        }
    }

    public static ClassPathLoader getInstance() {
        if (ClassPathLoader.Holder.INSTANCE == null) {
            throw new IllegalStateException("ClasspathLoader is not initialized. Use ClasspathLoader.init().");
        }
        return ClassPathLoader.Holder.INSTANCE;
    }

    public void start(String[] args){
        File dependenciesDir = new File(dependenciesLocation);
        try {
            String classpath = "E:\\Projects\\conqueror;" + dependenciesDir.getCanonicalPath() + "\\*";

            ProcessBuilder processB = this.processBuilder
                    .command("java", "-cp", classpath, "Conqueror");

            if (args != null) {
                for (String arg : args) {
                    processB.command().add(arg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
