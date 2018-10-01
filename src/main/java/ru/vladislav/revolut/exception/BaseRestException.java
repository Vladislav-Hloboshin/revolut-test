package ru.vladislav.revolut.exception;

public abstract class BaseRestException extends RuntimeException {

    public abstract int getHttpCode();

    public BaseRestException() {
        super();
    }

    public BaseRestException(String message) {
        super(message);
    }

    public BaseRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseRestException(Throwable cause) {
        super(cause);
    }
}
