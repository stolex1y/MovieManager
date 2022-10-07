package ru.stolexiy.server.commands;

import ru.stolexiy.data.User;
import ru.stolexiy.server.MovieManager;

public class CommandNotFoundCommand extends AbstractCommand<Nothing, String> {

    public CommandNotFoundCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String execute(User user) {
        return "Команда не найдена.";
    }
}
