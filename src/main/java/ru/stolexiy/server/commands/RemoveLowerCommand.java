package ru.stolexiy.server.commands;

import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.FailedCommandExecutionException;
import ru.stolexiy.server.MovieManager;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Класс, описывающий команду, которая удаляет из коллекции все фильмы, меньшие, чем заданный
 */
public class RemoveLowerCommand extends AbstractCommand<String, String> {

    public RemoveLowerCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String executeWithArg(User user, String argument) throws FailedCommandExecutionException {
        try {
            movieManager.getMoviesLock().lock();

            List<Integer> list = movieManager
                    .getMovies()
                    .values()
                    .stream()
                    .filter(f -> f.getName().compareTo(argument) < 0)
                    .filter(f -> f.getOwner().equals(user.getLogin()))
                    .map(Movie::getId)
                    .collect(Collectors.toList());

            AtomicInteger removed = new AtomicInteger();
            AtomicReference<Exception> exception = new AtomicReference<>(null);
            list.forEach(m -> {
                try {
                    movieManager.getMovieRepository().delete(m);
                    movieManager.getMovies().remove(m);
                    removed.getAndIncrement();
                } catch (SQLException e) {
                    exception.set(e);
                }
            });

            if (list.isEmpty())
                throw new FailedCommandExecutionException("Ни один фильм не удалён.");
            else if (removed.get() != list.size())
                throw new FailedCommandExecutionException(exception.get());

            return "Удалено " + removed.get() + " фильм(-ов/-а).";
        } finally {
            movieManager.getMoviesLock().unlock();
        }

    }
}
