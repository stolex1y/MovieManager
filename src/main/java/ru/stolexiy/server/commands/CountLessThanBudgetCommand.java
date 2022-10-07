package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.MovieManager;

/**
 * Класс, описывающий команду, считающую количество фильмов, бюджет которых меньше заданного
 */
public class CountLessThanBudgetCommand extends AbstractCommand<Number, Long> {

    public CountLessThanBudgetCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public Long executeWithArg(User user, Number argument) {
        long budget = argument.longValue();
        return movieManager
                .getMovies()
                .values()
                .stream()
                .map(Movie::getBudget)
                .filter(f -> (f < budget))
                .count();
    }
}
