package ru.vladislav.revolut.exception;

public class BadRequestException extends BaseRestException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getHttpCode() {
        return /*Bad Request*/400;
    }
}
