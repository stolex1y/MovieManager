package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;

public class RemoveLowerCommand extends AbstractCommand {

    @Override
    public String execute(IO io) {
        io.printString("Введите название фильма: ");
        return io.readString();
    }
}
