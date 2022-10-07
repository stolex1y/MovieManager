package ru.stolexiy.server.commands;

import ru.stolexiy.data.User;
import ru.stolexiy.server.MovieManager;

public class EmptyCommand extends AbstractCommand<Nothing, String> {

    public EmptyCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String execute(User user) {
        return "";
    }
}
