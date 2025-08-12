package framework.src.server.processors.context;

import configuration.Configuration;
import framework.src.server.annotations.component.ComponentEntity;
import framework.src.server.annotations.component.configuration.ComponentConfig;
import framework.src.server.annotations.component.configuration.ForceInstance;
import framework.src.server.exceptions.CircularDependencyException;
import framework.src.server.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * ApplicationContext is a class that manages the application context for a server.
 * It handles the registration and retrieval of components, entities, and annotations.
 * The context is initialized with a configuration and an executor service.
 */
public final class ApplicationContext {

    private final String PACKAGE;
    private final File FILE;
    private final Map<Class<?>, Object> APPLICATION_COMPONENTS = new LinkedHashMap<>();
    private final Set<Class<?>> APPLICATION_ENTITIES = new LinkedHashSet<>();
    private final Set<Class<? extends Annotation>> COMPONENT_ANNOTATIONS = new LinkedHashSet<>();

    public ApplicationContext(Configuration configuration, ExecutorService executorService) {
        this.force(configuration.getClass(), configuration);
        this.force(executorService.getClass(), executorService);
        this.force(this.getClass(), this);
        Path resultPath = Path.of(configuration.readProperty("project.result.path"))
                .normalize();
        this.FILE = resultPath.toFile();
        this.PACKAGE = configuration.readProperty("project.package");
    }

    /**
     * Registers a custom annotation that will be recognized as a component.
     *
     * @param annotation The annotation class to register.
     */
    public void registerAnnotation(Class<? extends Annotation> annotation) {
        this.COMPONENT_ANNOTATIONS.add(annotation);
    }

    /**
     * Checks if the given class has any of the registered component annotations.
     *
     * @param clazz The class to check for component annotations.
     * @return true if the class has any registered component annotations, false otherwise.
     */
    public boolean hasAcceptedAnnotations(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(COMPONENT_ANNOTATIONS::contains);
    }

    /**
     * Requests an instance of the specified class from the application context.
     * If the instance is not found, it throws a NoSuchElementException.
     *
     * @param clazz The class type of the instance to request.
     * @param <T>   The type of the instance.
     * @return The instance of the requested class.
     * @throws NoSuchElementException if the instance is not registered in the application context.
     */
    public <T> T requestInstance(Class<T> clazz) {
        T instance = this.requestInstanceOrNull(clazz);
        if (instance == null) {
            throw new NoSuchElementException(clazz.getName() + " not registered in Application Context.");
        }
        return instance;
    }

    /**
     * Requests an instance of the specified class from the application context.
     * If the instance is not found, it returns null.
     *
     * @param clazz The class type of the instance to request.
     * @param <T>   The type of the instance.
     * @return The instance of the requested class, or null if not found.
     */
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

    /**
     * Retrieves all components registered in the application context.
     *
     * @return A map containing all components, where the key is the class type and the value is the instance.
     */
    public Map<Class<?>, Object> getComponents() {
        return Map.copyOf(this.APPLICATION_COMPONENTS);
    }

    /**
     * Retrieves all entities registered in the application context.
     *
     * @return A set containing all entity classes.
     */
    public Set<Class<?>> getEntities() {
        return Set.copyOf(this.APPLICATION_ENTITIES);
    }

    /**
     * Initializes the application context by scanning for components and entities in the specified package.
     * It gathers all classes, handles logging, and resolves circular dependencies.
     *
     * @throws IOException            If an I/O error occurs while reading files.
     * @throws ClassNotFoundException If a class cannot be found during the scanning process.
     */
    public void applicationContextInit() throws IOException, ClassNotFoundException {
        System.out.println(Arrays.toString(this.FILE.listFiles()));
        List<File> files = Arrays.asList(Objects.requireNonNull(this.FILE.listFiles()));
        List<Class<?>> allComponents = gather(files, PACKAGE);
        Object circularDependency = null; // in case is circular
        Iterator<Class<?>> iteratorAllComponents = allComponents.listIterator();

        while (iteratorAllComponents.hasNext()) {
            Class<?> component = iteratorAllComponents.next();

            this.handleLogging(component);

            if (component.isAnnotationPresent(ComponentEntity.class)) {
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
            this.APPLICATION_COMPONENTS.entrySet().forEach(System.out::println);
            throw new CircularDependencyException("Circular dependency or missing @Component: " + circularDependency);
        }
    }

    /**
     * Handles the configuration of components annotated with @ComponentConfig.
     * It invokes methods annotated with @ForceInstance to create and register instances in the application context.
     *
     * @param componentConfig The class annotated with @ComponentConfig to process.
     */
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
                    Object methodResult = methodTurn.invoke(instanceComponentConfig.get());
                    this.APPLICATION_COMPONENTS.put(methodResult.getClass(), methodResult);
                } else {
                    List<Object> args = this.rezolveConstructorArgs(methodTurn.getParameterTypes());
                    Object methodResult = methodTurn.invoke(instanceComponentConfig.get(), args.toArray());
                    this.APPLICATION_COMPONENTS.put(methodResult.getClass(), methodResult);
                }
                iterator.remove();
                Logger.log(this.getClass(), "Initialization from component config: " + methodTurn.getReturnType().getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Checks if all parameters of a method can be resolved to instances in the application context.
     *
     * @param parameters The parameters of the method to check.
     * @return true if all parameters can be resolved, false otherwise.
     */
    private boolean checkParameters(Parameter[] parameters) {
        for (Parameter p : parameters) {
            System.out.println(p.getType());
            if (this.requestInstanceOrNull(p.getType()) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles logging for components, distinguishing between entities and other components.
     *
     * @param component The class of the component being registered or initialized.
     */
    private void handleLogging(Class<?> component) {
        if (component.isAnnotationPresent(ComponentEntity.class)) {
            Logger.log(this.getClass(), "Register Entity: " + component.getName());
            return;
        }
        Logger.log(this.getClass(), "Initialization component: " + component.getName());
    }

    /**
     * Gathers all classes from the specified files and package, filtering by accepted annotations.
     *
     * @param files        The list of files to scan for classes.
     * @param packageName  The package name to prepend to class names.
     * @return A list of classes that have accepted annotations.
     * @throws ClassNotFoundException If a class cannot be found during the scanning process.
     * @throws IOException            If an I/O error occurs while reading files.
     */
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
                if (this.hasAcceptedAnnotations(classTest)) {
                    allComponents.add(classTest);
                }
            }
            return allComponents;
        }
    }

    /**
     * Forces an instance of the specified class to be registered in the application context.
     * This method allows manual registration of components, bypassing the usual instantiation process.
     *
     * @param clazz    The class type of the instance to force.
     * @param instance The instance to register in the application context.
     */
    public void force(Class<?> clazz, Object instance) {
        APPLICATION_COMPONENTS.put(clazz, instance);
    }

    /**
     * Retrieves the constructor of the specified class that has the most parameters.
     * If the class is an interface, it throws an InstantiationException.
     *
     * @param clazz The class to get the constructor from.
     * @return The constructor with the most parameters.
     * @throws InstantiationException If the class is an interface or no constructor is found.
     */
    private Constructor<?> getConstructor(Class<?> clazz) throws InstantiationException {
        if (clazz.isInterface()) {
            throw new InstantiationException("Can't instantiate an interface: " + clazz.getName());
        }
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new NoSuchElementException("No constructor has been found."));
    }

    /**
     * Resolves the constructor arguments for the specified constructor.
     * It checks the parameter types and retrieves instances from the application context.
     *
     * @param constructor The constructor to resolve arguments for.
     * @return A list of resolved arguments for the constructor.
     */
    private List<Object> rezolveConstructorArgs(Constructor<?> constructor) {
        return this.rezolveConstructorArgs(constructor.getParameterTypes());
    }

    /**
     * Resolves the constructor arguments for the specified parameter types.
     * It checks each parameter type and retrieves instances from the application context.
     *
     * @param parameterTypes The array of parameter types to resolve.
     * @return A list of resolved arguments for the constructor.
     */
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

    /**
     * Safely creates an instance of the specified class using its constructor.
     * It resolves constructor arguments and handles exceptions that may occur during instantiation.
     *
     * @param component The class to create an instance of.
     * @return An Optional containing the created instance, or empty if instantiation fails.
     */
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
