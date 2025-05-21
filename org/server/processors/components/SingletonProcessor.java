package org.server.processors.components;

import org.server.configuration.Configuration;
import org.server.exepltions.CircularDependencyException;
import org.server.processors.components.annotations.Singleton;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public final class SingletonProcessor {

    private final String PACKAGE;
    private final File FILE;
    private final Map<Class<?>, Object> APPLICATION_CONTEXT = new LinkedHashMap<>();

    public SingletonProcessor(Configuration configuration, ExecutorService executorService) {
        this.force(configuration.getClass(), configuration);
        this.force(executorService.getClass(), executorService);
        String path = configuration.readProperty("project.path");
        this.FILE = new File(path);
        this.PACKAGE = configuration.readProperty("project.package");
    }

    @SuppressWarnings("unchecked")
    public <T> T requestInstance(Class<T> clazz) {
        Object instance = APPLICATION_CONTEXT.get(clazz);

        if (instance != null) {
            return (T) instance;
        }

        if (clazz.isInterface()) {
            for (Map.Entry<Class<?>, Object> entry : APPLICATION_CONTEXT.entrySet()) {
                Class<?> candidateClass = entry.getKey();
                if (clazz.isAssignableFrom(candidateClass)) {
                    return (T) entry.getValue();
                }
            }
        }

        throw new NoSuchElementException(clazz.getName() + " not registered in Application Context.");
    }

    public Map<Class<?>, Object> getContext() {
        return Map.copyOf(this.APPLICATION_CONTEXT);
    }

    public void applicationContextInit() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<File> files = Arrays.asList(Objects.requireNonNull(this.FILE.listFiles()));
        List<Class<?>> allSingletons = getAllSingletons(files, PACKAGE);
        List<String> argsNamesForError = null; //in case there is an error for debugging

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
                List<Object> args = this.rezolveConstructorArgs(constructor);
                if (args.stream().anyMatch(Objects::isNull)) {
                    argsNamesForError = args.stream()
                            .map(a -> a == null ? null : a.getClass().getName())
                            .toList();
                    continue;
                }
                instance = constructor.newInstance(args.toArray());
            }

            System.out.println("[" + this.getClass().getSimpleName() + "] Initialization -> " + singleton.getName());
            APPLICATION_CONTEXT.put(singleton, instance);
            iteratorAllSingletons.remove();

            iteratorAllSingletons = allSingletons.listIterator(); //Reset
        }

        if (!allSingletons.isEmpty()) {
            List<String> finalArgsForError = argsNamesForError;
            allSingletons.forEach(s -> {
                System.err.println(s);
                System.err.println(finalArgsForError);
            });
            throw new CircularDependencyException("Circular dependency or missing @Singleton: " + allSingletons);
        }
    }

    private List<Object> rezolveConstructorArgs(Constructor<?> constructor){
        return Arrays.stream(constructor.getParameterTypes())
                .map(p -> {
                    AtomicReference<Object> arg = new AtomicReference<>();
                    if (p.isInterface()) {
                        APPLICATION_CONTEXT.forEach((key, value) -> {
                            if (Arrays.stream(key.getInterfaces()).toList().contains(p)) {
                                arg.set(value);
                            }
                        });
                    } else {
                        arg.set(APPLICATION_CONTEXT.get(p));
                    }
                    return arg.get();
                })
                .toList();
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
                if (!classTest.isAnnotationPresent(Singleton.class)) {
                    continue;
                }
                allSingletons.add(classTest);
            }
            return allSingletons;
        }
    }

    public void force(Class<?> clazz, Object instance) {
        APPLICATION_CONTEXT.put(clazz, instance);
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();
    }
}
