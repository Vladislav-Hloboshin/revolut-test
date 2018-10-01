package ru.vladislav.revolut;

import com.querydsl.sql.HSQLDBTemplates;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.vladislav.revolut.controller.DefaultWebController;
import ru.vladislav.revolut.exception.BaseRestException;
import ru.vladislav.revolut.querydsl.QueryDSLDataSource;
import ru.vladislav.revolut.repository.AccountRepository;
import ru.vladislav.revolut.service.MoneyService;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static spark.Spark.*;

public class EntryPoint {

    static DataSource createDataSource() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:hsqldb:mem:localhost");
        config.setUsername("sa");
        config.setPassword("");
        config.setLeakDetectionThreshold(5*1000);
        DataSource ds = new HikariDataSource(config);

        // create table and populate data
        try(Connection connection = ds.getConnection()) {
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE account(id INTEGER PRIMARY KEY, balance BIGINT NOT NULL);");
            statement.execute("INSERT INTO account(id, balance) VALUES 0,1000000;");
            statement.execute("INSERT INTO account(id, balance) VALUES 1,1000000;");
        }
        return ds;
    }

    public static void main(String[] args) throws SQLException {
        QueryDSLDataSource queryDSLDataSource = new QueryDSLDataSource(createDataSource(), HSQLDBTemplates.DEFAULT);
        AccountRepository accountRepository = new AccountRepository();
        MoneyService moneyService = new MoneyService(queryDSLDataSource, accountRepository);
        DefaultWebController defaultWebController = new DefaultWebController(moneyService);

        post("/transaction", defaultWebController::createTransaction);

        get("/accounts/:accountId/balance", defaultWebController::getBalance);

        exception(BaseRestException.class, (exception, request, response) -> {
            response.status( ((BaseRestException)exception).getHttpCode() );
            response.body(exception.getMessage());
        });

    }
}
