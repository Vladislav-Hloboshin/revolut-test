package ru.vladislav.revolut.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;

import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class QueryDSLConnection implements AutoCloseable {

    private final Connection connection;
    private final SQLTemplates dialect;

    public QueryDSLConnection(Connection connection, SQLTemplates dialect) {
        this.connection = connection;
        this.dialect = dialect;
    }

    public Connection getConnection() {
        return connection;
    }

    public SQLQuery<?> query() {
        return new SQLQuery<Void>(connection, dialect);
    }

    public <T> SQLQuery<T> select(Expression<T> expr) {
        return query().select(expr);
    }

    public SQLQuery<Tuple> select(Expression<?>... exprs) {
        return query().select(exprs);
    }

    public SQLInsertClause insert(RelationalPath<?> entity) {
        return new SQLInsertClause(connection, dialect, entity);
    }

    public SQLUpdateClause update(RelationalPath<?> entity) {
        return new SQLUpdateClause(connection, dialect, entity);
    }

    public void close() throws SQLException {
        connection.close();
    }

}
