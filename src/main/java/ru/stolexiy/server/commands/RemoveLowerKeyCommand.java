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
 * Класс, определяющий команду, удаляющую из коллекции все фильмы, ключ которых меньше, чем заданный
 */
public class RemoveLowerKeyCommand extends AbstractCommand<Integer, String> {

    public RemoveLowerKeyCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String executeWithArg(User user, Integer argument) throws FailedCommandExecutionException {
        try {
            movieManager.getMoviesLock().lock();

            List<Integer> list = movieManager
                    .getMovies()
                    .values()
                    .stream()
                    .filter(movie -> movie.getOwner().equals(user.getLogin()))
                    .map(Movie::getId)
                    .filter(id -> id < argument)
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
            else if (removed.get() == list.size())
                throw new FailedCommandExecutionException(exception.get());

            return "Удалено " + removed.get() + " фильм(-ов/-а).";
        } finally {
            movieManager.getMoviesLock().unlock();
        }
    }
}
