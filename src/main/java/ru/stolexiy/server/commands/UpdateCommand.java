package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.FailedCommandExecutionException;
import ru.stolexiy.server.IllegalAccessException;
import ru.stolexiy.server.MovieManager;

import java.sql.SQLException;

/**
 * Класс, описывающий команду, которая изменяет фильм по его id
 */
public class UpdateCommand extends AbstractCommand<Movie, String> {

    public UpdateCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String executeWithArg(User user, Movie argument) throws FailedCommandExecutionException {
        if (!movieManager.getMovies().containsKey(argument.getId())) {
            throw new FailedCommandExecutionException("Фильма с данным id " + argument.getId() + " не существует.");
        }
        try {
            movieManager.getMoviesLock().lock();
            if (!movieManager.getMovies().get(argument.getId()).getOwner().equals(user.getLogin()))
                throw new IllegalAccessException();
            movieManager.getMovieRepository().updateByUserAndId(user, argument);
            movieManager.getMovies().replace(argument.getId(),
                    movieManager.getMovieRepository().get(argument.getId()));
        } catch (IllegalAccessException | SQLException e) {
            throw new FailedCommandExecutionException(e);
        } finally {
            movieManager.getMoviesLock().unlock();
        }
        return "Фильм с данным id " + argument.getId() + " изменён.";
    }
}
