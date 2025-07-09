package src.com.app.repository;

import src.com.server.annotations.component.Component;
import src.com.config.mysql.MySqlPersistence;
import src.com.config.mysql.utils.ColumnValueConvertor;
import src.com.config.mysql.utils.PreparedStatementSetter;
import src.com.config.mysql.utils.schemaHandler.schemaManager.FieldConvertor;
import src.com.config.mysql.utils.schemaHandler.schemaManager.SchemaManager;
import src.com.config.mysql.utils.schemaHandler.schemaManager.SqlStatements;

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
