package ru.stolexiy.server.commands;

import ru.stolexiy.data.User;
import ru.stolexiy.server.FailedCommandExecutionException;
import ru.stolexiy.server.MovieManager;

import java.sql.SQLException;

/**
 * Класс, описывающий команду, которая очищает коллекцию
 */
public class ClearCommand extends AbstractCommand<Nothing, String> {

    public ClearCommand(String name, String description, MovieManager movieManager) {
        super(name, description, movieManager);
    }

    @Override
    public String execute(User user) throws FailedCommandExecutionException {
        try {
            movieManager.getMoviesLock().lock();
            movieManager.getMovies().clear();
            movieManager.getMovieRepository().deleteByUser(user);
            movieManager.getMovieRepository().getAll().forEach(m -> movieManager.getMovies().put(m.getId(), m));
        } catch (SQLException e) {
            throw new FailedCommandExecutionException(e);
        } finally {
            movieManager.getMoviesLock().unlock();
        }
        return "Ваша коллекция очищена.";
    }
}
