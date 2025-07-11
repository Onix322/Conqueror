package loader.utilities;

import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ClassPathLoader {

    private final String dependenciesLocation;
    private final Configuration configuration;
    private final ExecutorService executorService;

    private ClassPathLoader(Configuration configuration, ExecutorService executorService) {
        this.dependenciesLocation = configuration.readProperty("dependencies.location");
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


        try (URLClassLoader appClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());) {
            Class<?> mainClass = appClassLoader.loadClass("src.com.App");
            mainClass.getDeclaredMethod("start", Configuration.class, ExecutorService.class)
                    .invoke(null, configuration, executorService);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 IOException e) {
            throw new RuntimeException(e);
        }
    }
}
