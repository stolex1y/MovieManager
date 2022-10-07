package ru.stolexiy.client.exceptions;

public class InternalClientException extends Exception {

    public InternalClientException() {
        super("Произошла внутренняя ошибка приложения.");
    }

    public InternalClientException(String message) {
        super(message);
    }

    public InternalClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalClientException(Throwable cause) {
        super(cause);
    }

}
