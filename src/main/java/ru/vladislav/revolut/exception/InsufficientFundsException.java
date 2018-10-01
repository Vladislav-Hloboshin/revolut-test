package ru.vladislav.revolut.exception;

public class InsufficientFundsException extends BaseRestException {

    public InsufficientFundsException(String message) {
        super(message);
    }

    @Override
    public int getHttpCode() {
        return /*Conflict*/409;
    }
}
