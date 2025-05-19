package org.server.processors;

import org.server.configuration.Configuration;
import org.server.configuration.ConfigurationImpl;
import org.server.exepltions.CircularDependencyException;
import org.server.processors.annotations.Singleton;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class SingletonProcessor {

    private final Configuration CONFIGURATION;
    private final String PATH;
    private final String PACKAGE;
    private final File FILE;
    private final Map<Class<?>, Object> APPLICATION_CONTEXT = new LinkedHashMap<>();

    public SingletonProcessor(Configuration configuration){
        this.CONFIGURATION = configuration;
        this.PATH = CONFIGURATION.readProperty("project.path");
        this.FILE = new File(PATH);
        this.PACKAGE = CONFIGURATION.readProperty("project.package");;
    }

    public <T> T requestInstance(Class<T> clazz){
        return clazz.cast(this.APPLICATION_CONTEXT.get(clazz));
    }

    public Map<Class<?>, Object> getContext(){
        return Map.copyOf(this.APPLICATION_CONTEXT);
    }

    public void applicationContextInit() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<File> files = Arrays.asList(Objects.requireNonNull(this.FILE.listFiles()));
        List<Class<?>> allSingletons = getAllSingletons(files, PACKAGE);

        Iterator<Class<?>> iteratorAllSingletons = allSingletons.listIterator();

        while (iteratorAllSingletons.hasNext()) {
            Class<?> singleton = iteratorAllSingletons.next();

            //already in context -> jump over
            if (APPLICATION_CONTEXT.containsKey(singleton)) {
                iteratorAllSingletons.remove();
                continue;
            }

            Constructor<?> constructor = getConstructor(singleton);
            constructor.setAccessible(true);
            Object instance;

            if (constructor.getParameterTypes().length < 1) {
                instance = constructor.newInstance();
            } else {
                List<Object> args = Arrays.stream(constructor.getParameterTypes())
                        .map(APPLICATION_CONTEXT::get)
                        .toList();

                if (args.stream().anyMatch(Objects::isNull)) {
                    continue;
                }
                instance = constructor.newInstance(args.toArray());
            }

            System.out.println("[Initialization] -> " + singleton.getName());
            APPLICATION_CONTEXT.put(singleton, instance);
            iteratorAllSingletons.remove();

            iteratorAllSingletons = allSingletons.listIterator(); //Reset
        }

        if (!allSingletons.isEmpty()) {
            throw new CircularDependencyException("Circular dependency or missing @Singleton: " + allSingletons);
        }
    }

    private List<Class<?>> getAllSingletons(List<File> files, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> allSingletons = new LinkedList<>();
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{FILE.toURI().toURL()})) {
            for (File f : files) {
                if (f.isDirectory()) {
                    List<File> inner_files = Arrays.stream(Objects.requireNonNull(f.listFiles())).toList();
                    allSingletons.addAll(getAllSingletons(inner_files, packageName + f.getName() + "."));
                }
                String fn = f.getName();
                if (!fn.endsWith(".class")) continue;
                Class<?> classTest = classLoader.loadClass(packageName + fn.substring(0, fn.length() - 6));
                if (!classTest.isAnnotationPresent(Singleton.class)) continue;
                allSingletons.add(classTest);
            }
            return allSingletons;
        }
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();
    }
}
