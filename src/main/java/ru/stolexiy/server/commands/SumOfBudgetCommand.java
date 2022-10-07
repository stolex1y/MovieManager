package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.MovieManager;

/**
 * Класс, описывающий команду, которая возвращает сумму значений поля бюджет для всех фильмов
 */
public class SumOfBudgetCommand extends AbstractCommand<Nothing, Long> {

    public SumOfBudgetCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public Long execute(User user) {
        return movieManager
                .getMovies()
                .values()
                .stream()
                .map(Movie::getBudget)
                .reduce(Long::sum)
                .orElse(0L);
    }
}
