package ru.vladislav.revolut.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.vladislav.revolut.controller.DefaultWebController;
import ru.vladislav.revolut.exception.BadRequestException;
import ru.vladislav.revolut.service.MoneyService;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultWebControllerTest {

    private MoneyService moneyService;
    private Request req;
    private Response res;

    private DefaultWebController defaultWebController;

    @Before
    public void init() {
        moneyService = Mockito.mock(MoneyService.class);
        req = Mockito.mock(Request.class);
        res = Mockito.mock(Response.class);

        defaultWebController = new DefaultWebController(moneyService);
    }

    @Test
    public void createTransactionTest() throws SQLException {
        when(req.contentType()).thenReturn("application/x-www-form-urlencoded");
        when(req.body()).thenReturn("accountIdFrom=0&accountIdTo=1&amount=100");

        defaultWebController.createTransaction(req, res);

        verify(moneyService).transfer(eq(0), eq(1), eq(100L));
    }

    @Test(expected = BadRequestException.class)
    public void createTransactionTest0_BadRequestException() throws SQLException {
        when(req.contentType()).thenReturn("incorect-content-type");


        defaultWebController.createTransaction(req, res);
    }

    @Test(expected = BadRequestException.class)
    public void createTransactionTest1_BadRequestException() throws SQLException {
        when(req.contentType()).thenReturn("application/x-www-form-urlencoded");
        when(req.body()).thenReturn("useless-data");


        defaultWebController.createTransaction(req, res);
    }

    @Test
    public void getBalanceTest() throws SQLException {
        when(req.params(eq("accountId"))).thenReturn("0");

        defaultWebController.getBalance(req, res);

        verify(moneyService).getBalance(eq(0));
    }

    @Test(expected = BadRequestException.class)
    public void getBalanceTest_BadRequestException() throws SQLException {
        when(req.params(eq("accountId"))).thenReturn("wrong-number");

        defaultWebController.getBalance(req, res);
    }

}
