package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.MpaaRating;
import ru.stolexiy.data.User;
import ru.stolexiy.server.MovieManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, описывающий команду, возвращающую фильмы, MPAA рейтинг которых равен заданному
 */
public class FilterByMpaaRatingCommand extends AbstractCommand<MpaaRating, List<Movie>> {

    public FilterByMpaaRatingCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public List<Movie> executeWithArg(User user, MpaaRating argument) {
        List<Movie> movies = movieManager
                .getMovies()
                .values()
                .stream()
                .filter(f -> f.getMpaaRating().equals(argument))
                .collect(Collectors.toList());
        if (movies.isEmpty())
            movies.add(null);
        return movies;
    }
}
