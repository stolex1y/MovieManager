package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.MovieManager;

/**
 * Класс, описывающий команду, которая считает среднее значение оскаров всех фильмов
 */
public class AverageOfOscarsCountCommand extends AbstractCommand<Nothing, Double> {

    public AverageOfOscarsCountCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public Double execute(User user) {
        return movieManager
                .getMovies()
                .values()
                .stream()
                .mapToLong(Movie::getOscarsCount)
                .average()
                .orElse(0);
    }
}
