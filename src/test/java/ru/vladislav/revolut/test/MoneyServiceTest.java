package ru.vladislav.revolut.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.vladislav.revolut.domain.AccountReadOnlyEntity;
import ru.vladislav.revolut.exception.InsufficientFundsException;
import ru.vladislav.revolut.exception.NotFoundException;
import ru.vladislav.revolut.querydsl.QueryDSLConnection;
import ru.vladislav.revolut.querydsl.QueryDSLDataSource;
import ru.vladislav.revolut.repository.AccountRepository;
import ru.vladislav.revolut.service.MoneyService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoneyServiceTest {

    private QueryDSLDataSource queryDSLDataSource;
    private QueryDSLConnection queryDSLConnection;
    private Connection connection;
    private AccountRepository accountRepository;

    private MoneyService moneyService;

    @Before
    public void init() throws SQLException {
        queryDSLDataSource = Mockito.mock(QueryDSLDataSource.class);
        queryDSLConnection = Mockito.mock(QueryDSLConnection.class);
        connection = Mockito.mock(Connection.class);
        accountRepository = Mockito.mock(AccountRepository.class);

        when(queryDSLDataSource.getConnection()).thenReturn(queryDSLConnection);
        when(queryDSLConnection.getConnection()).thenReturn(connection);

        moneyService = new MoneyService(queryDSLDataSource, accountRepository);
    }

    @Test
    public void transferTest() throws SQLException {
        int accountIdFrom = 0;
        int accountIdTo = 1;
        long initialBalance = 1000_000L;
        long amount = 100;

        AccountReadOnlyEntity accountFrom = Mockito.mock(AccountReadOnlyEntity.class);
        when(accountFrom.getId()).thenReturn(accountIdFrom);
        when(accountFrom.getBalance()).thenReturn(initialBalance);

        AccountReadOnlyEntity accountTo = Mockito.mock(AccountReadOnlyEntity.class);
        when(accountTo.getId()).thenReturn(accountIdTo);
        when(accountTo.getBalance()).thenReturn(initialBalance);

        when(accountRepository.findAllByIdsWithLock(
                eq(queryDSLConnection),
                eq(Arrays.asList(accountIdFrom, accountIdTo)))
        ).thenReturn(Arrays.asList(accountFrom, accountTo));


        moneyService.transfer(accountIdFrom, accountIdTo, amount);


        verify(accountRepository).updateBalanceById(
                eq(queryDSLConnection),
                eq(initialBalance - amount),
                eq(accountIdFrom)
        );
        verify(accountRepository).updateBalanceById(
                eq(queryDSLConnection),
                eq(initialBalance + amount),
                eq(accountIdTo)
        );
    }

    @Test(expected = NotFoundException.class)
    public void transferTest_NotFoundException() throws SQLException {
        when(accountRepository.findAllByIdsWithLock(
                eq(queryDSLConnection),
                any())
        ).thenReturn(Collections.emptyList());


        moneyService.transfer(0, 1, 100);
    }

    @Test(expected = InsufficientFundsException.class)
    public void transferTest_InsufficientFundsException() throws SQLException {
        int accountIdFrom = 0;
        int accountIdTo = 1;

        AccountReadOnlyEntity accountFrom = Mockito.mock(AccountReadOnlyEntity.class);
        when(accountFrom.getId()).thenReturn(accountIdFrom);
        when(accountFrom.getBalance()).thenReturn(0L);

        AccountReadOnlyEntity accountTo = Mockito.mock(AccountReadOnlyEntity.class);
        when(accountTo.getId()).thenReturn(accountIdTo);
        when(accountTo.getBalance()).thenReturn(0L);

        when(accountRepository.findAllByIdsWithLock(
                eq(queryDSLConnection),
                eq(Arrays.asList(accountIdFrom, accountIdTo)))
        ).thenReturn(Arrays.asList(accountFrom, accountTo));


        moneyService.transfer(accountIdFrom, accountIdTo, 100);
    }

    @Test
    public void getBalanceTest() throws SQLException {
        int accountId = 0;
        long initialBalance = 1000_000L;

        AccountReadOnlyEntity account = Mockito.mock(AccountReadOnlyEntity.class);
        when(account.getId()).thenReturn(accountId);
        when(account.getBalance()).thenReturn(initialBalance);

        when(accountRepository.findOneById(eq(queryDSLConnection), eq(accountId))).thenReturn(account);


        long accountBalance = moneyService.getBalance(accountId);


        assertEquals(initialBalance, accountBalance);
    }

    @Test(expected = NotFoundException.class)
    public void getBalanceTest_NotFoundException() throws SQLException {
        moneyService.getBalance(0);
    }

}
