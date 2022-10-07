package ru.stolexiy.client;

import ru.stolexiy.client.console.IO;
import ru.stolexiy.exceptions.InterruptionOfCommandException;
import ru.stolexiy.client.console.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class CommandManager {
    private IO io;
    private static final Map<String, AbstractCommand> commands = new HashMap<>();

    public CommandManager(IO io) {
        this.io = io;

        commands.put("count_greater_than_mpaa_rating", new CountGreaterThanMpaaRatingCommand());
        commands.put("count_less_than_budget", new CountLessThanBudgetCommand());
        commands.put("execute_script", new ExecuteScriptCommand());
        commands.put("filter_by_mpaa_rating", new FilterByMpaaRatingCommand());
        commands.put("insert", new InsertCommand());
        commands.put("remove_key", new RemoveKeyCommand());
        commands.put("remove_lower", new RemoveLowerCommand());
        commands.put("remove_lower_key", new RemoveLowerKeyCommand());
        commands.put("update", new UpdateCommand());
        commands.put("exit", new ExitCommand());
    }

    /**
     * Метод для считывания названия команды
     *
     * @return прочитанное название команды
     */
    public String readCommand() {
        try {
            io.printString("Введите команду:");
            return io.readNotEmptyString();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Метод для считывания аргументов команды
     *
     * @param commandName название команды
     * @return результат выполнения команды
     * @throws InterruptionOfCommandException если необходимо
     *                                        прервать выполнение команды из-за некорректного ввода данных
     */
    public Object readCommandArg(String commandName) throws InterruptionOfCommandException {
        AbstractCommand command = commands.get(commandName);
        if (command == null)
            return null;
        else
            return command.execute(io);
    }

    public void exit() {
        ((ExitCommand) commands.get("exit")).execute(io);
    }
}
