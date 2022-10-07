package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;
import ru.stolexiy.data.Movie;

public class InsertCommand extends AbstractCommand {

    @Override
    public Movie execute(IO io) {
        return io.readMovie();
    }
}
