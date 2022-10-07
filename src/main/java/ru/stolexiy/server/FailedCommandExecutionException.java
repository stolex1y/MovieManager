package ru.stolexiy.server;

public class FailedCommandExecutionException extends Exception {
    public FailedCommandExecutionException() {
        super();
    }

    public FailedCommandExecutionException(String message) {
        super(message);
    }

    public FailedCommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedCommandExecutionException(Throwable cause) {
        super(cause);
    }
}
