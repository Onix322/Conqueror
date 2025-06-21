package src.test.singletonAnnotationTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class SingletonAnnotationTest {
    static Map<Class<?>, Object> application_singletons_context = new LinkedHashMap<>();
    static String path = "out/production/conqueror/test/singletonAnnotationTest";
    static File file = new File(path);

    public static void applicationContextInit() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<File> files = Arrays.asList(Objects.requireNonNull(file.listFiles()));
        System.out.println(Arrays.toString(file.list()));
        String packageName = "test.singletonAnnotationTest.";
        List<Class<?>> allSingletons = getAllSingletons(files, packageName);

        Iterator<Class<?>> iteratorAllSingletons = allSingletons.listIterator();

        while (iteratorAllSingletons.hasNext()) {
            Class<?> singleton = iteratorAllSingletons.next();

            //already in context -> jump over
            if (application_singletons_context.containsKey(singleton)) {
                iteratorAllSingletons.remove();
                continue;
            }

            Constructor<?> constructor = getConstructor(singleton);
            Object instance;

            if (constructor.getParameterTypes().length < 1) {
                instance = constructor.newInstance();
            } else {
                List<Object> args = Arrays.stream(constructor.getParameterTypes())
                        .map(application_singletons_context::get)
                        .toList();

                if (args.stream().anyMatch(Objects::isNull)) {
                    continue;
                }
                instance = constructor.newInstance(args.toArray());
            }

            application_singletons_context.put(singleton, instance);
            iteratorAllSingletons.remove();

            iteratorAllSingletons = allSingletons.listIterator(); //Reset
        }

        if (!allSingletons.isEmpty()) {
            throw new CircularDependencyException("Circular dependency or missing @Singleton: " + allSingletons);
        }

        application_singletons_context.forEach((k, v) -> {
            System.out.println(v);
        });
    }


    //! GET RID OF RECURSIVE
    public static List<Class<?>> getAllSingletons(List<File> files, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> allSingletons = new LinkedList<>();
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()})) {
            for (File f : files) {
                if (f.isDirectory()) {
                    List<File> inner_files = Arrays.stream(Objects.requireNonNull(f.listFiles())).toList();
                    System.out.println(packageName + f.getName() + ".");
                    allSingletons.addAll(getAllSingletons(inner_files, packageName + f.getName() + "."));
                }
                String fn = f.getName();
                if (!fn.endsWith(".class")) continue;
                Class<?> classTest = classLoader.loadClass(packageName + fn.substring(0, fn.length() - 6));
                if (!classTest.isAnnotationPresent(TestSingleton.class)) continue;
                allSingletons.add(classTest);
            }
            return allSingletons;
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();
    }

    public static void main(String[] args) throws Exception {
        applicationContextInit();
        System.out.println(application_singletons_context.get(SingletonClassTest.class).toString());
    }
}
