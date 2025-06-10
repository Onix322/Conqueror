package org.app.repository;

import org.app.entity.TestObject;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.FieldConvertor;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SchemaManager;
import org.server.managers.database.databaseManager.schemaHandler.schemaManager.SqlStatements;
import org.server.managers.database.persistence.ColumnValueConvertor;
import org.server.managers.database.persistence.MySqlPersistence;
import org.server.managers.database.persistence.PreparedStatementSetter;
import org.server.processors.context.annotations.Component;

@Component
public class TestObjectRepository extends MySqlPersistence<TestObject, Integer> {
    protected TestObjectRepository(SqlStatements sqlStatements, FieldConvertor fieldConvertor, SchemaManager schemaManager, PreparedStatementSetter statementSetter, ColumnValueConvertor columnValueConvertor) {
        super(sqlStatements, fieldConvertor, schemaManager, statementSetter, columnValueConvertor);
    }
}
