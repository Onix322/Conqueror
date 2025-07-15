package build_tool.utilities;

import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * ClassPathLoader is responsible for loading the classpath dynamically
 * based on the dependencies specified in the configuration.
 * It initializes the application by loading the main class and invoking its start method.
 */
public class ClassPathLoader {

    private final String dependenciesLocation;
    private final String targetLocation;
    private final Configuration configuration;
    private final ExecutorService executorService;

    private ClassPathLoader(Configuration configuration, ExecutorService executorService) {
        this.dependenciesLocation = configuration.readProperty("dependencies.location");
        this.targetLocation = configuration.readProperty("target.location");
        this.configuration = configuration;
        this.executorService = executorService;
    }

    private static class Holder {
        private static ClassPathLoader INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration, ExecutorService executorService) {
        if (ClassPathLoader.Holder.INSTANCE == null) {
            ClassPathLoader.Holder.INSTANCE = new ClassPathLoader(configuration, executorService);
        }
    }

    public static ClassPathLoader getInstance() {
        if (ClassPathLoader.Holder.INSTANCE == null) {
            throw new IllegalStateException("ClasspathLoader is not initialized. Use ClasspathLoader.init().");
        }
        return ClassPathLoader.Holder.INSTANCE;
    }

    /**
     * Starts the application by loading the classpath and invoking the main class's start method.
     * It scans the dependencies directory for JAR files and loads them into a URLClassLoader.
     * The main class is expected to be located at "src.com.App" and must have a static start method
     * that accepts Configuration and ExecutorService as parameters.
     */
    public void start(){
        File libDir = new File(dependenciesLocation);
        URL[] urls = Arrays.stream(Objects.requireNonNull(libDir.listFiles()))
                .filter(f -> f.getName().endsWith(".jar"))
                .map(f -> {
                    try {
                        return f.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(URL[]::new);


        try (URLClassLoader appClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader())) {
            this.createClassPathTxt();
            Class<?> mainClass = appClassLoader.loadClass("framework.src.App");
            mainClass.getDeclaredMethod("start", Configuration.class, ExecutorService.class)
                    .invoke(null, configuration, executorService);

        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a classpath.txt file in the target location containing the paths of all JAR files
     * found in the dependencies directory. This file is used to set the classpath for the application.
     *
     * @throws IOException if there is an error creating or writing to the classpath.txt file
     */
    private void createClassPathTxt() throws IOException {
        File libDir = new File(dependenciesLocation);
        StringBuilder classPath = new StringBuilder();
        classPath.append("java -cp ");
        for (File file : Objects.requireNonNull(libDir.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                classPath.append(file.getCanonicalPath()).append(File.pathSeparator);
            }
        }
        // Remove the last path separator
        if (!classPath.isEmpty()) {
            classPath.setLength(classPath.length() - 1);
        }
        classPath.append(" Main");
        this.writeClassPathToFile(classPath.toString());
    }

    /**
     * Writes the classpath to a file named classpath.txt in the target location.
     * If the file does not exist, it creates it.
     *
     * @param classPath the classpath string to write to the file
     * @throws IOException if there is an error writing to the file
     */
    private void writeClassPathToFile(String classPath) throws IOException {
        File classPathFile = new File(targetLocation, "classpath.txt");
        if(!classPathFile.exists()) {
            Files.createFile(classPathFile.toPath());
        }
        Files.write(Path.of(classPathFile.getCanonicalPath()), classPath.getBytes());
        System.out.println(classPathFile);
    }
}
