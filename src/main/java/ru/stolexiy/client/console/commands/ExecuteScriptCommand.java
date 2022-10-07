package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;
import ru.stolexiy.client.exceptions.InternalClientException;
import ru.stolexiy.connection.CommandWithArgument;
import ru.stolexiy.client.*;
import ru.stolexiy.exceptions.InterruptionOfCommandException;

import java.io.*;
import java.util.*;

public class ExecuteScriptCommand extends AbstractCommand {

    private static final Set<String> openedFiles = new HashSet<>();

    @Override
    public List<CommandWithArgument> execute(IO io) throws InterruptionOfCommandException {
        File file = io.readFileName();
        if (file == null)
            throw new InterruptionOfCommandException();
        List<CommandWithArgument> commandsList = new ArrayList<>();
        try {
            if (openedFiles.contains(file.getAbsolutePath()))
                throw new InterruptionOfCommandException("Зацикленный вызов скриптов.");
            else
                openedFiles.add(file.getAbsolutePath());
            try (FileInputStream reader = new FileInputStream(file)) {
                IO scriptIO = new IO(reader, null);
                Client scriptClient = new Client(scriptIO);
                try {
                    while (scriptIO.hasNext()) {
                        String commandName = scriptClient.getCommandManager().readCommand();
                        Object commandArg = scriptClient.getCommandManager().readCommandArg(commandName);
                        commandsList.add(new CommandWithArgument(commandName, commandArg));
                    }
                } catch (NoSuchElementException e) {
                    throw new InterruptionOfCommandException("Неправильный формат скрипта.");
                }
            }
        } catch (InternalClientException | IOException e) {
            throw new InterruptionOfCommandException();
        } finally {
            openedFiles.remove(file.getAbsolutePath());
        }
        return commandsList;
    }
}