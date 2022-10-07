package ru.stolexiy.client.console.commands;

import ru.stolexiy.client.console.IO;
import ru.stolexiy.data.Movie;

public class UpdateCommand extends AbstractCommand {

    @Override
    public Object execute(IO io) {
        io.printString("Введите id фильма: ");
        int id = io.readInt();
        Movie movie = io.readMovie();
        movie.setId(id);
        return movie;
    }
}
