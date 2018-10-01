package ru.vladislav.revolut.controller;

import ru.vladislav.revolut.exception.BadRequestException;
import ru.vladislav.revolut.service.MoneyService;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultWebController {

    private final MoneyService moneyService;

    public DefaultWebController(MoneyService moneyService) {
        this.moneyService = moneyService;
    }

    public String createTransaction(Request req, @SuppressWarnings("unused") Response res) throws SQLException {
        if(!"application/x-www-form-urlencoded".equals(req.contentType())) {
            throw new BadRequestException("ContentType must be 'application/x-www-form-urlencoded'");
        }

        int accountIdFrom;
        int accountIdTo;
        long amount;
        try {
            Map<String,String> params = Arrays.stream(req.body().split("&"))
                    .map(x->x.split("="))
                    .collect(Collectors.toMap(x->x[0], x->x[1]));

            accountIdFrom = Integer.parseInt(params.get("accountIdFrom"));
            accountIdTo = Integer.parseInt(params.get("accountIdTo"));
            amount = Long.parseLong(params.get("amount"));
        } catch (Exception e) {
            throw new BadRequestException(e);
        }

        moneyService.transfer(accountIdFrom, accountIdTo, amount);
        return "SUCCESS";
    }

    public long getBalance(Request req, @SuppressWarnings("unused") Response res) throws SQLException {
        int accountId;
        try {
            accountId = Integer.parseInt(req.params("accountId"));
        } catch (NumberFormatException e) {
            throw new BadRequestException(e);
        }

        return moneyService.getBalance(accountId);
    }

}
