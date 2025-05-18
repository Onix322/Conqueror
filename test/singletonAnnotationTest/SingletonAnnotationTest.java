package test.singletonAnnotationTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
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
        List<Class<?>> waiting = new LinkedList<>();

        Iterator<Class<?>> iteratorAllSingletons = allSingletons.iterator();

        while (iteratorAllSingletons.hasNext()) {
            Class<?> singleton = iteratorAllSingletons.next();

            if(application_singletons_context.containsKey(singleton)){
                iteratorAllSingletons.remove();
                continue;
            }
            Constructor<?> constructor = getConstructor(singleton);
            if(constructor.getParameterTypes().length > 0) {
                waiting.add(singleton);
                iteratorAllSingletons.remove();
                continue;
            }

            Object instance = constructor.newInstance();
            application_singletons_context.put(singleton, instance);
            iteratorAllSingletons.remove();
        }

        Iterator<Class<?>> waitingIterator = waiting.iterator();

        while (waitingIterator.hasNext()) {
            Class<?> singleton = waitingIterator.next();

            if(application_singletons_context.containsKey(singleton)){
                waitingIterator.remove();
                continue;
            }
            Constructor<?> constructor = getConstructor(singleton);
            Object[] args = Arrays.stream(constructor.getParameterTypes())
                    .map(o -> application_singletons_context.get(o))
                    .toList()
                    .toArray();

            Object instance = constructor.newInstance(args);
            application_singletons_context.put(singleton, instance);
            waitingIterator.remove();
        }


        System.out.println(waiting);
        System.out.println(allSingletons);
        System.out.println(application_singletons_context);
    }

    public static List<Class<?>> getAllSingletons(List<File> files, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> allSingletons = new LinkedList<>();
        try (URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()})) {
            for(File f : files){
                if(f.isDirectory()) {
                    List<File> inner_files = Arrays.stream(Objects.requireNonNull(f.listFiles())).toList();
                    System.out.println(packageName + f.getName() + ".");
                    allSingletons.addAll(getAllSingletons(inner_files, packageName + f.getName() + "."));
                }
                String fn = f.getName();
                if (!fn.endsWith(".class")) continue;
                Class<?> classTest = classLoader.loadClass( packageName + fn.substring(0, fn.length() - 6));
                if (!classTest.isAnnotationPresent(Singleton.class)) continue;
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
