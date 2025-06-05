package org.server.managers.database.databaseManager.schemaHandler.schemaManager;

import org.server.managers.database.databaseManager.entityData.EntityColumn;
import org.server.managers.database.databaseManager.entityData.EntityTable;
import org.server.processors.context.annotations.Component;

import java.sql.JDBCType;
import java.util.List;

/*
* Factoring SQL strings for SchemaManager usage.
* */
@Component
public class SqlQueryFactory {

    private SqlQueryFactory() {
    }

    public String createTableSql(EntityTable entityTable) {

        //TODO 1. column per field
        //TODO 2. annotations for columns with attributes (name, uniques, nullable, primary_key)
        //TODO 3. annotations for relationships (1-1, 1-N, N-1, N-N);

        return "CREATE TABLE "
                + entityTable.getName()
                + this.slqEntityColumnsFormat(entityTable.getColumns());
    }

    public String deleteTableSql(EntityTable entityTable) {
        return "DROP TABLE " + entityTable.getName();
    }

    public String existTableSql(EntityTable entityTable){
        return "SELECT EXISTS (" +
                "  SELECT 1" +
                "  FROM INFORMATION_SCHEMA.TABLES" +
                "  WHERE TABLE_NAME = '" + entityTable.getName() +
                "') AS table_exists";
    }

    /*
     * Is creating sql table column syntax e.g: column_name VARCHAR(255) UNIQUE NOT NULL
     * */
    public String sqlFormat(EntityColumn entityColumn) {
        String base = entityColumn.getColumnName() + " " + entityColumn.getType();

        if (entityColumn.getType().equals(JDBCType.VARCHAR)) base += "(255)";
        if (entityColumn.isPrimaryKey()) return base + " PRIMARY KEY";
        if (entityColumn.isUnique()) base += " UNIQUE";
        if (entityColumn.isNullable()) base += " NOT NULL";
        return base;

    }

    /*
     * Is creating MULTIPLE sql table column syntax e.g:
     * (column_name1 VARCHAR(255) UNIQUE NOT NULL, column_name2 VARCHAR(255) UNIQUE NOT NULL)
     * */
    public String slqEntityColumnsFormat(List<EntityColumn> entityColumns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');

        for (int i = 0; i < entityColumns.size(); i++) {
            EntityColumn ec = entityColumns.get(i);
            stringBuilder.append(this.sqlFormat(ec));
            if (i < entityColumns.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
