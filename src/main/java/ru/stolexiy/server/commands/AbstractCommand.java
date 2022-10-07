package ru.stolexiy.server.commands;

import ru.stolexiy.data.User;
import ru.stolexiy.server.FailedCommandExecutionException;
import ru.stolexiy.server.MovieManager;

/**
 * Суперкласс для всех команд серверного приложения,
 * которые занимаются выполнением команд пользователя
 *
 * @param <T> тип аргумента команды
 * @param <R> тип объекта, возвращаемого командой
 */
public abstract class AbstractCommand<T, R> {
    protected final String name;
    protected final String description;
    protected final MovieManager movieManager;

    /**
     * @param name         название команды
     * @param description  описание команды
     * @param movieManager объект класса, работающего с коллекцией фильмов
     */
    public AbstractCommand(String name, String description, MovieManager movieManager) {
        this.name = name;
        this.description = description;
        this.movieManager = movieManager;
    }

    /**
     * Метод для выполнения команды с аргументом
     *
     * @param argument аргумент команды
     * @return результат выполнения команды
     * @throws UnsupportedOperationException   если происходит попытка выполнить команду с аргументом или без,
     *                                         которая не предполагает такого
     * @throws IllegalArgumentException        если происходит попытка выполнить команду с аргументом неправильного типа
     * @throws FailedCommandExecutionException если выполнение команды завершилось неуспешно
     */
    public final R execute(User user, Object argument) throws FailedCommandExecutionException,
            IllegalArgumentException,
            UnsupportedOperationException {
        if (argument != null) {
            try {
                return executeWithArg(user, (T) argument);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Недопустимый аргумент");
            }
        } else
            throw new IllegalArgumentException("Аргумент не может быть null");
    }

    /**
     * Метод для выполнения команды без аргументов
     *
     * @return результат выполнения команды
     */
    public R execute(User user) throws UnsupportedOperationException,
            FailedCommandExecutionException {
        throw new UnsupportedOperationException();
    }

    /**
     * Метод для выполнения команды с аргументом
     *
     * @param argument аргумент команды
     * @return результат выполнения команды
     */
    public R executeWithArg(User user, T argument) throws UnsupportedOperationException,
            FailedCommandExecutionException {
        throw new UnsupportedOperationException();
    }

}

interface Nothing {
}
