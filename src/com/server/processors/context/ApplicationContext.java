package src.com.server.processors.context;

import src.com.server.annotations.component.Component;
import src.com.server.annotations.component.configuration.ComponentConfig;
import src.com.server.annotations.component.configuration.ForceInstance;
import src.com.server.annotations.controller.Controller;
import src.com.server.annotations.entity.Entity;
import src.com.server.configuration.Configuration;
import src.com.server.exceptions.CircularDependencyException;
import src.com.server.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class ApplicationContext {

    private final String PACKAGE;
    private final File FILE;
    private final Map<Class<?>, Object> APPLICATION_COMPONENTS = new LinkedHashMap<>();
    private final Set<Class<?>> APPLICATION_ENTITIES = new LinkedHashSet<>();

    public ApplicationContext(Configuration configuration, ExecutorService executorService) {
        this.force(configuration.getClass(), configuration);
        this.force(executorService.getClass(), executorService);
        this.force(this.getClass(), this);
        String path = configuration.readProperty("project.path");
        this.FILE = new File(path);
        this.PACKAGE = configuration.readProperty("project.package");
    }

    public <T> T requestInstance(Class<T> clazz) {
        T instance = this.requestInstanceOrNull(clazz);
        if (instance == null) {
            throw new NoSuchElementException(clazz.getName() + " not registered in Application Context.");
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T requestInstanceOrNull(Class<T> clazz) {
        Object instance = APPLICATION_COMPONENTS.get(clazz);

        if (instance != null) {
            return (T) instance;
        }

        if (clazz.isInterface()) {
            for (Map.Entry<Class<?>, Object> entry : APPLICATION_COMPONENTS.entrySet()) {
                Class<?> candidateClass = entry.getKey();
                if (clazz.isAssignableFrom(candidateClass)) {
                    return (T) entry.getValue();
                }
            }
        }

        return null;
    }

    public Map<Class<?>, Object> getComponents() {
        return Map.copyOf(this.APPLICATION_COMPONENTS);
    }

    public Set<Class<?>> getEntities() {
        return Set.copyOf(this.APPLICATION_ENTITIES);
    }

    public void applicationContextInit() throws IOException, ClassNotFoundException {
        List<File> files = Arrays.asList(Objects.requireNonNull(this.FILE.listFiles()));
        List<Class<?>> allComponents = gather(files, PACKAGE);
        Object circularDependency = null; // in case is circular
        Iterator<Class<?>> iteratorAllComponents = allComponents.listIterator();

        while (iteratorAllComponents.hasNext()) {
            Class<?> component = iteratorAllComponents.next();

            this.handleLogging(component);

            if (component.isAnnotationPresent(Entity.class)) {
                APPLICATION_ENTITIES.add(component);
                iteratorAllComponents.remove();
                continue;
            }

            if (component.isAnnotationPresent(ComponentConfig.class)) {
                this.handleComponentConfig(component);
                iteratorAllComponents.remove();
                continue;
            }

            //already in context -> jump over
            if (APPLICATION_COMPONENTS.containsKey(component)) {
                iteratorAllComponents.remove();
                continue;
            }

            Optional<Object> instance = this.safeInstance(component);
            if (instance.isEmpty()) continue;

            APPLICATION_COMPONENTS.put(component, instance.get());
            iteratorAllComponents.remove();
            circularDependency = component;
            iteratorAllComponents = allComponents.listIterator(); //Reset
        }

        if (!allComponents.isEmpty()) {
            allComponents.forEach(System.err::println);
            throw new CircularDependencyException("Circular dependency or missing @Component: " + circularDependency);
        }
    }

    private void handleComponentConfig(Class<?> componentConfig) {

        Optional<Object> instanceComponentConfig = this.safeInstance(componentConfig);
        Deque<Method> methods = Arrays.stream(componentConfig.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(ForceInstance.class))
                .collect(Collectors.toCollection(LinkedBlockingDeque::new));

        Iterator<Method> iterator = methods.iterator();

        while (iterator.hasNext()) {
            Method methodTurn = iterator.next();
            Parameter[] parameters = methodTurn.getParameters();
            if (!this.checkParameters(parameters)) {
                methods.addLast(methods.removeFirst());
                iterator = methods.iterator();
                continue;
            }

            try {
                if (methodTurn.getParameters().length == 0) {
                    this.APPLICATION_COMPONENTS.put(methodTurn.getReturnType(), methodTurn.invoke(instanceComponentConfig.get()));
                } else {
                    List<Object> args = this.rezolveConstructorArgs(methodTurn.getParameterTypes());
                    this.APPLICATION_COMPONENTS.put(methodTurn.getReturnType(), methodTurn.invoke(instanceComponentConfig.get(), args.toArray()));
                }
                iterator.remove();
                Logger.log(this.getClass(), "Initialization from component config: " + methodTurn.getReturnType().getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean checkParameters(Parameter[] parameters) {
        for (Parameter p : parameters) {
            System.out.println(p.getType());
            if (this.requestInstanceOrNull(p.getType()) == null) {
                return false;
            }
        }
        return true;
    }

    private void handleLogging(Class<?> component) {
        if (component.isAnnotationPresent(Entity.class)) {
            Logger.log(this.getClass(), "Register Entity: " + component.getName());
            return;
        }
        Logger.log(this.getClass(), "Initialization component: " + component.getName());
    }

    private List<Class<?>> gather(List<File> files, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> allComponents = new LinkedList<>();
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{FILE.toURI().toURL()})) {
            for (File f : files) {
                if (f.isDirectory()) {
                    List<File> inner_files = Arrays.stream(Objects.requireNonNull(f.listFiles())).toList();
                    allComponents.addAll(gather(inner_files, packageName + f.getName() + "."));
                }
                String fn = f.getName();
                if (!fn.endsWith(".class")) continue;
                Class<?> classTest = classLoader.loadClass(packageName + fn.substring(0, fn.length() - 6));
                if (classTest.isAnnotationPresent(Component.class) ||
                        classTest.isAnnotationPresent(Controller.class) ||
                        classTest.isAnnotationPresent(ComponentConfig.class) ||
                        classTest.isAnnotationPresent(Entity.class)) {
                    allComponents.add(classTest);
                }
            }
            return allComponents;
        }
    }

    public void force(Class<?> clazz, Object instance) {
        APPLICATION_COMPONENTS.put(clazz, instance);
    }

    private Constructor<?> getConstructor(Class<?> clazz) throws InstantiationException {
        if (clazz.isInterface()) {
            throw new InstantiationException("Can't instantiate an interface: " + clazz.getName());
        }
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new NoSuchElementException("No constructor has been found."));
    }

    private List<Object> rezolveConstructorArgs(Constructor<?> constructor) {
        return this.rezolveConstructorArgs(constructor.getParameterTypes());
    }

    private List<Object> rezolveConstructorArgs(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(p -> {
                    AtomicReference<Object> arg = new AtomicReference<>();
                    if (p.isInterface()) {
                        APPLICATION_COMPONENTS.forEach((key, value) -> {
                            if (Arrays.stream(key.getInterfaces()).toList().contains(p)) {
                                arg.set(value);
                            }
                        });
                    } else {
                        arg.set(APPLICATION_COMPONENTS.get(p));
                    }
                    return arg.get();
                })
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Optional<Object> safeInstance(Class<?> component) {
        try {
            Constructor<?> constructor = getConstructor(component);
            constructor.setAccessible(true);

            if (constructor.getParameterTypes().length >= 1) {
                List<Object> args = this.rezolveConstructorArgs(constructor);
                if (args.stream().anyMatch(Objects::isNull)) {
                    return Optional.empty();
                }
                return Optional.of(constructor.newInstance(args.toArray()));
            }

            return Optional.of(constructor.newInstance());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            System.err.println(e.getCause().fillInStackTrace().getMessage());
            throw new RuntimeException("Unable to instantiate: " + component.getName());
        }
    }
}
