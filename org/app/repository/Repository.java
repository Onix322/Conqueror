package org.app.repository;

import org.server.database.mysql.utils.schemaHandler.schemaManager.FieldConvertor;
import org.server.database.mysql.utils.schemaHandler.schemaManager.SchemaManager;
import org.server.database.mysql.utils.schemaHandler.schemaManager.SqlStatements;
import org.server.database.mysql.utils.ColumnValueConvertor;
import org.server.database.mysql.MySqlPersistence;
import org.server.database.mysql.utils.PreparedStatementSetter;
import org.server.annotations.component.Component;

@Component
public class Repository<T> extends MySqlPersistence<T, Integer> {
    protected Repository(SqlStatements sqlStatements, FieldConvertor fieldConvertor, SchemaManager schemaManager, PreparedStatementSetter statementSetter, ColumnValueConvertor columnValueConvertor) {
        super(sqlStatements, fieldConvertor, schemaManager, statementSetter, columnValueConvertor);
    }
}
