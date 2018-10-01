package ru.vladislav.revolut.service;

import ru.vladislav.revolut.domain.AccountReadOnlyEntity;
import ru.vladislav.revolut.exception.InsufficientFundsException;
import ru.vladislav.revolut.exception.NotFoundException;
import ru.vladislav.revolut.querydsl.QueryDSLConnection;
import ru.vladislav.revolut.querydsl.QueryDSLDataSource;
import ru.vladislav.revolut.repository.AccountRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class MoneyService {

    private final QueryDSLDataSource queryDSLDataSource;
    private final AccountRepository accountRepository;

    public MoneyService(QueryDSLDataSource queryDSLDataSource, AccountRepository accountRepository) {
        this.queryDSLDataSource = queryDSLDataSource;
        this.accountRepository = accountRepository;
    }

    public void transfer(int accountIdFrom, int accountIdTo, long amount) throws SQLException {
        try(QueryDSLConnection queryDSLConn = queryDSLDataSource.getConnection()) {
            try {
                queryDSLConn.getConnection().setAutoCommit(false);

                List<AccountReadOnlyEntity> accounts = accountRepository.findAllByIdsWithLock(queryDSLConn, Arrays.asList(accountIdFrom, accountIdTo));

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
                for(AccountReadOnlyEntity account : accounts) {
                    //noinspection ConstantConditions
                    int accountId = account.getId();

                    if(accountId == accountIdFrom) {
                        accountBalanceFrom = account.getBalance();
                    } else if(accountId == accountIdTo) {
                        accountBalanceTo = account.getBalance();
                    }
                }

                if(accountBalanceFrom == null || accountBalanceTo == null) {
                    throw new RuntimeException("Something went wrong");
                }

                if(accountBalanceFrom < amount) {
                    throw new InsufficientFundsException(
                            String.format("There are not enough funds on the account (%s)", accountIdFrom)
                    );
                }

                accountRepository.updateBalanceById(queryDSLConn, accountBalanceFrom - amount, accountIdFrom);
                accountRepository.updateBalanceById(queryDSLConn, accountBalanceTo + amount, accountIdTo);

                queryDSLConn.getConnection().commit();
            } catch (Exception e) {
                queryDSLConn.getConnection().rollback();
                throw e;
            }
        }
    }

    public long getBalance(int accountId) throws SQLException {
        try(QueryDSLConnection queryDSLConn = queryDSLDataSource.getConnection()) {
            AccountReadOnlyEntity account = accountRepository.findOneById(queryDSLConn, accountId);
            if(account == null) {
                throw new NotFoundException(String.format("Account (%s) not found", accountId));
            }
            return account.getBalance();
        }
    }

}
