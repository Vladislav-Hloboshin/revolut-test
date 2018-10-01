package ru.vladislav.revolut.exception;

public class NotFoundException extends BaseRestException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public int getHttpCode() {
        return /*Not Found*/404;
    }
}
