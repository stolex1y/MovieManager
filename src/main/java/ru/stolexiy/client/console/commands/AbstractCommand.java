package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;
import ru.stolexiy.exceptions.InterruptionOfCommandException;

/**
 * Суперкласс для всех команд клиентского приложения,
 * которые занимаются получением данных от пользователя
 */
public abstract class AbstractCommand {
    /**
     * @param io объект, представляющий модуль ввода/вывода
     * @return аргумент команды, полученный от пользователя
     * @throws InterruptionOfCommandException если необходимо
     *                                        прервать выполнение команды из-за некорректного ввода данных
     */
    public abstract Object execute(IO io) throws InterruptionOfCommandException;
}

