package org.server.managers.databaseManager;

import org.server.exepltions.IllegalClassException;
import org.server.managers.driverManager.ConnectionManager;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

@Component
public class DatabaseManager {
    private final ConnectionManager CONNECTION_MANAGER;
    private final Statement STATEMENT;
    private DatabaseManager(ConnectionManager connection){
        this.CONNECTION_MANAGER = connection;
        try {
            this.STATEMENT = connection.connect().createStatement();
        } catch (SQLException | MalformedURLException | URISyntaxException | ClassNotFoundException |
                 InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String createTableSql(Class<?> entity) {
        if(!entity.isAnnotationPresent(Entity.class)){
            throw new IllegalClassException("Class: " + entity + " is not defined as entity. Missing of @Entity annotation.");
        }

        Field[] fields = entity.getDeclaredFields();

        //?Fa documentare despre tipurile de date din mysql
        //TODO fa coloanele dupa filedurile entitatii

        System.out.println(Arrays.toString(fields));

        return "CREATE TABLE " + this.getEntityValue(entity) + '(';
    }

    private String getEntityValue(Class<?> entity){
        return entity.getAnnotation(Entity.class).value();
    }

    public void autoload(Set<Class<?>> applicationEntities) throws SQLException {
        Iterator<Class<?>> entityIterator = applicationEntities.stream().iterator();
        while (entityIterator.hasNext()){
            Class<?> entity = entityIterator.next();
            String tableSql = this.createTableSql(entity);
            System.out.println(this.STATEMENT.execute(tableSql));
        }
    }

    public void stop() throws SQLException {
        System.out.println();
        this.STATEMENT.close();
        this.CONNECTION_MANAGER.close();
    }
}
