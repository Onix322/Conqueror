package org.app.repository;

import org.server.managers.database.databaseManager.schemaHandler.schemaManager.FieldConvertor;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SchemaManager;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SqlStatements;
import org.server.managers.database.persistence.mysql.ColumnValueConvertor;
import org.server.managers.database.persistence.mysql.MySqlPersistence;
import org.server.managers.database.persistence.mysql.PreparedStatementSetter;

public class Repository<T> extends MySqlPersistence<T, Integer> {
    protected Repository(SqlStatements sqlStatements, FieldConvertor fieldConvertor, SchemaManager schemaManager, PreparedStatementSetter statementSetter, ColumnValueConvertor columnValueConvertor) {
        super(sqlStatements, fieldConvertor, schemaManager, statementSetter, columnValueConvertor);
    }
}
