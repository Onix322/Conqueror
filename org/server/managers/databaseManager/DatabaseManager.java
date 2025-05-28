package org.server.managers.databaseManager;

import org.server.exepltions.IllegalClassException;
import org.server.managers.driverManager.ConnectionManager;
import org.server.processors.context.annotations.Component;
import org.server.processors.context.annotations.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseManager {
    private final ConnectionManager CONNECTION_MANAGER;
    private final DatabaseFieldConvertor DB_FIELD_CONVERTOR;

    private DatabaseManager(ConnectionManager connection, DatabaseFieldConvertor databaseFieldConvertor) {
        this.CONNECTION_MANAGER = connection;
        this.DB_FIELD_CONVERTOR = databaseFieldConvertor;
    }

    public PreparedStatement preparedStatement(String sql) throws RuntimeException {
        try {
            return this.CONNECTION_MANAGER.connect()
                    .prepareStatement(sql);
        } catch (SQLException | MalformedURLException | URISyntaxException | ClassNotFoundException |
                 InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String createTableSql(Class<?> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new IllegalClassException("Class: " + entity + " is not defined as entity. Missing of @Entity annotation.");
        }

        Field[] fields = entity.getDeclaredFields();

        //?Fa documentare despre tipurile de date din mysql
        //* Create type convertor
        //TODO 1. column per field
        //TODO 2. annotations for columns with attributes (name, uniques, nullable, primary_key)
        //TODO 3. annotations for relationships (1-1, 1-N, N-1, N-N)
        List<EntityColumn> entityColumns = this.convertFields(fields);

        EntityTable entityTable = new EntityTable(
                entity.getAnnotation(Entity.class).name(),
                entityColumns
        );

        return "CREATE TABLE "
                + entityTable.getName()
                + this.DB_FIELD_CONVERTOR.slqEntityColumnsFormat(entityColumns);
    }

    private List<EntityColumn> convertFields(Field[] fields) {
        List<EntityColumn> entityColumns = new LinkedList<>();

        for (Field field : fields) {
            EntityColumn entityColumn = this.DB_FIELD_CONVERTOR.convertor(field);
            entityColumns.add(entityColumn);
        }

        return entityColumns;
    }

    public void autoload(Set<Class<?>> applicationEntities) throws SQLException {
        Iterator<Class<?>> entityIterator = applicationEntities.stream().iterator();
        while (entityIterator.hasNext()) {
            Class<?> entity = entityIterator.next();
            String tableSql = this.createTableSql(entity);
            this.preparedStatement(tableSql).execute();
        }
    }

    public void stop() throws SQLException {
        System.out.println();
        this.CONNECTION_MANAGER.close();
    }
}
