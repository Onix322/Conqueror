package src.com.server.managers.entityManager;


import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public sealed interface EntityManager permits EntityManagerImpl {

    <T> boolean contains(Class<T> clazz);

    void autoload() throws MalformedURLException, SQLException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
