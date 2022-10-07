package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;

public class ExitCommand extends AbstractCommand {

    @Override
    public String execute(IO io) {
        System.exit(0);
        return null;
    }
}
