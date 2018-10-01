package ru.vladislav.revolut.controller;

import com.querydsl.core.Tuple;
import ru.vladislav.revolut.QueryDSLDataSource;
import ru.vladislav.revolut.domain.QAccountEntity;
import ru.vladislav.revolut.exception.InsufficientFundsException;
import ru.vladislav.revolut.exception.NotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoneyController {

    // for typed queries
    private static final QAccountEntity qAccount = QAccountEntity.account;

    public void transfer(int accountIdFrom, int accountIdTo, long amount) throws SQLException {
        try(QueryDSLDataSource.QueryDSLConnection queryDSLConn = QueryDSLDataSource.getConnection()) {
            try {
                queryDSLConn.connection.setAutoCommit(false);

                List<Tuple> accounts = queryDSLConn.select(qAccount.id, qAccount.balance)
                        .from(qAccount)
                        .where(qAccount.id.in(accountIdFrom, accountIdTo))
                        .forUpdate()
                        .fetch();

                if(accounts.size() < 2) {
                    throw new NotFoundException(
                            String.format("One or all of the accounts (%s) or (%s) not found", accountIdFrom, accountIdTo)
                    );
                }
                if(accounts.size() > 2) {
                    throw new RuntimeException("It can not be");
                }

                Long accountBalanceFrom = null;
                Long accountBalanceTo = null;
                for(Tuple account : accounts) {
                    //noinspection ConstantConditions
                    int accountId = account.get(qAccount.id);
                    //noinspection ConstantConditions
                    long accountBalance = account.get(qAccount.balance);

                    if(accountId == accountIdFrom) {
                        accountBalanceFrom = accountBalance;
                    } else if(accountId == accountIdTo) {
                        accountBalanceTo = accountBalance;
                    }
                }

                if(accountBalanceFrom == null || accountBalanceTo == null) {
                    throw new RuntimeException();
                }

                if(accountBalanceFrom < amount) {
                    throw new InsufficientFundsException(
                            String.format("There are not enough funds on the account (%s)", accountIdFrom)
                    );
                }

                queryDSLConn.update(qAccount)
                        .set(qAccount.balance, accountBalanceFrom - amount)
                        .where(qAccount.id.eq(accountIdFrom))
                        .execute();
                queryDSLConn.update(qAccount)
                        .set(qAccount.balance, accountBalanceTo + amount)
                        .where(qAccount.id.eq(accountIdTo))
                        .execute();

                queryDSLConn.connection.commit();
            } catch (Exception e) {
                queryDSLConn.connection.rollback();
                throw e;
            }
        }
    }

    public long getBalance(int accountId) throws SQLException {
        List<Long> balances;
        try(QueryDSLDataSource.QueryDSLConnection queryDSLConnection = QueryDSLDataSource.getConnection()) {
            balances = queryDSLConnection.select(qAccount.balance)
                    .from(qAccount)
                    .where(qAccount.id.eq(accountId))
                    .fetch();
        }
        if(balances.size() == 0) {
            throw new NotFoundException(String.format("Account (%s) not found", accountId));
        }
        return balances.get(0);
    }

}
