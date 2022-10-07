package ru.stolexiy.exceptions;

/**
 * Выбрасывается, если необходимо прервать нормальное выполнение команды
 */
public class InterruptionOfCommandException extends Exception {

    public InterruptionOfCommandException() {
        super();
    }

    public InterruptionOfCommandException(String message) {
        super(message);
    }
}
