package ru.stolexiy.server;

/**
 * Выбрасывается, если невозможно открыть файл или файл не существует
 */
public class InvalidFileException extends Exception {

    public InvalidFileException() {
    }

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFileException(Throwable cause) {
        super(cause);
    }
}
