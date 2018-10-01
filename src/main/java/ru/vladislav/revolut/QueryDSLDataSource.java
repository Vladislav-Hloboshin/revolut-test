package ru.vladislav.revolut;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryDSLDataSource {

    private static final HikariDataSource ds;
    private static final SQLTemplates dialect;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:hsqldb:mem:localhost");
        config.setUsername("sa");
        config.setPassword("");
        config.setLeakDetectionThreshold(5*1000);
        ds = new HikariDataSource(config);

        dialect = HSQLDBTemplates.DEFAULT;

        // create table and populate data
        try(Connection connection = ds.getConnection()) {
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE account(id INTEGER PRIMARY KEY, balance BIGINT NOT NULL);");
            statement.execute("INSERT INTO account(id, balance) VALUES 0,1000000;");
            statement.execute("INSERT INTO account(id, balance) VALUES 1,1000000;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private QueryDSLDataSource() {}

    public static QueryDSLConnection getConnection() throws SQLException {
        Connection connection = ds.getConnection();
        return new QueryDSLConnection(connection);
    }

    public static class QueryDSLConnection implements AutoCloseable {
        public final Connection connection;

        private QueryDSLConnection(Connection connection) {
            this.connection = connection;
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

}
