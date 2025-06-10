package org.app.repository;

import org.server.database.mysql.schemaHandler.schemaManager.FieldConvertor;
import org.server.database.mysql.schemaHandler.schemaManager.SchemaManager;
import org.server.database.mysql.schemaHandler.schemaManager.SqlStatements;
import org.server.database.mysql.ColumnValueConvertor;
import org.server.database.mysql.MySqlPersistence;
import org.server.database.mysql.PreparedStatementSetter;

public class Repository<T> extends MySqlPersistence<T, Integer> {
    protected Repository(SqlStatements sqlStatements, FieldConvertor fieldConvertor, SchemaManager schemaManager, PreparedStatementSetter statementSetter, ColumnValueConvertor columnValueConvertor) {
        super(sqlStatements, fieldConvertor, schemaManager, statementSetter, columnValueConvertor);
    }
}
