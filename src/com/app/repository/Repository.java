package src.com.app.repository;

import src.com.server.annotations.component.Component;
import src.com.server.database.mysql.MySqlPersistence;
import src.com.server.database.mysql.utils.ColumnValueConvertor;
import src.com.server.database.mysql.utils.PreparedStatementSetter;
import src.com.server.database.mysql.utils.schemaHandler.schemaManager.FieldConvertor;
import src.com.server.database.mysql.utils.schemaHandler.schemaManager.SchemaManager;
import src.com.server.database.mysql.utils.schemaHandler.schemaManager.SqlStatements;

@Component
public class Repository<T> extends MySqlPersistence<T, Integer> {
    protected Repository(SqlStatements sqlStatements,
                         FieldConvertor fieldConvertor,
                         SchemaManager schemaManager,
                         PreparedStatementSetter statementSetter,
                         ColumnValueConvertor columnValueConvertor) {
        super(sqlStatements, fieldConvertor, schemaManager, statementSetter, columnValueConvertor);
    }
}
