package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;

public class RemoveLowerKeyCommand extends AbstractCommand {

    @Override
    public Integer execute(IO io) {
        io.printString("Введите id фильма: ");
        return io.readInt(0);
    }
}
