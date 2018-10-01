package ru.vladislav.revolut.querydsl;

import com.querydsl.sql.SQLTemplates;

import javax.sql.DataSource;
import java.sql.SQLException;

public class QueryDSLDataSource {

    private final DataSource dataSource;
    private final SQLTemplates dialect;

    public QueryDSLDataSource(DataSource dataSource, SQLTemplates dialect) {
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    public QueryDSLConnection getConnection() throws SQLException {
        return new QueryDSLConnection(dataSource.getConnection(), dialect);
    }

}
