package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.FailedCommandExecutionException;
import ru.stolexiy.server.IllegalAccessException;
import ru.stolexiy.server.MovieManager;

import java.sql.SQLException;

/**
 * Класс, описывающий команду, которая удаляет фильм из коллекции по его id
 */
public class RemoveKeyCommand extends AbstractCommand<Integer, String> {

    public RemoveKeyCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String executeWithArg(User user, Integer argument) throws FailedCommandExecutionException {
        try {
            movieManager.getMoviesLock().lock();
            if (movieManager.getMovies().containsKey(argument)) {
                Movie movie = movieManager.getMovies().get(argument);
                try {
                    if (!movie.getOwner().equals(user.getLogin()))
                        throw new IllegalAccessException();
                    movieManager.getMovieRepository().delete(argument);
                } catch (SQLException | IllegalAccessException e) {
                    throw new FailedCommandExecutionException(e);
                }
                movieManager.getMovies().remove(argument);
            } else
                throw new FailedCommandExecutionException("Фильм не найден.");
        } finally {
            movieManager.getMoviesLock().unlock();
        }
        return "Фильм удалён.";
    }
}
