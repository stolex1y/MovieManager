package ru.stolexiy.server.commands;

import ru.stolexiy.connection.CommandWithArgument;
import ru.stolexiy.data.User;
import ru.stolexiy.server.FailedCommandExecutionException;
import ru.stolexiy.server.MovieManager;

import java.util.*;

/**
 * Класс, описывающий команду, которая считывает и исполняет скрипт
 */
public class ExecuteScriptCommand extends AbstractCommand<List<CommandWithArgument>, List<Object>> {

    public ExecuteScriptCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public List<Object> executeWithArg(User user, List<CommandWithArgument> argument) throws FailedCommandExecutionException {
        List<Object> result = new ArrayList<>();
        for (CommandWithArgument commandWithArgument : argument) {
            String name = commandWithArgument.getName();
            Object argumentCommand = commandWithArgument.getArgument();
            result.add(movieManager.executeCommand(user, new CommandWithArgument(name, argumentCommand)));
        }
        return result;
    }
}
