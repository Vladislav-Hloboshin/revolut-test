package ru.vladislav.revolut;

import org.slf4j.LoggerFactory;
import ru.vladislav.revolut.controller.MoneyController;
import ru.vladislav.revolut.exception.BadRequestException;
import ru.vladislav.revolut.exception.BaseRestException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

public class EntryPoint {
    private static final MoneyController moneyController = new MoneyController();

    public static void main(String[] args) {

        post("/transaction", (req, res) -> {
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

            moneyController.transfer(accountIdFrom, accountIdTo, amount);
            return "SUCCESS";
        });

        get("/accounts/:accountId/balance", (req, res) -> {
            int accountId;
            try {
                accountId = Integer.parseInt(req.params("accountId"));
            } catch (NumberFormatException e) {
                throw new BadRequestException(e);
            }
            
            return moneyController.getBalance(accountId);
        });

        exception(BaseRestException.class, (exception, request, response) -> {
            response.status( ((BaseRestException)exception).getHttpCode() );
            response.body(exception.getMessage());
        });

    }
}
