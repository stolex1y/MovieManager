package ru.stolexiy.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.connection.CommandWithArgument;
import ru.stolexiy.data.Movie;
import ru.stolexiy.data.User;
import ru.stolexiy.server.commands.*;
import ru.stolexiy.server.database.MovieRepository;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс для управления коллекцией фильмов с помощью команд
 */
public class MovieManager {

    private static final Logger logger = LogManager.getLogger("ServerLogger");
    private final Map<Integer, Movie> movies = new HashMap<>();
    private final Map<String, AbstractCommand<?, ?>> commands = new HashMap<>();
    private final Map<User, Queue<AbstractCommand<?, ?>>> commandHistory = new HashMap<>();
    private final AbstractCommand<?, ?> commandNotFound = new CommandNotFoundCommand(null, null, this);
    private final MovieRepository movieRepository;
    private final Lock moviesLock = new ReentrantLock();

    public MovieManager(MovieRepository movieRepository) throws SQLException {
        this.movieRepository = movieRepository;
        movieRepository.getAll().forEach(m -> movies.put(m.getId(), m));
        logger.info("Коллекция проинициализирована, размер коллекции: " + movies.size());
        createCommands();
    }

    private void createCommands() {
        commands.put("help", new HelpCommand("help", "вывести справку по доступным командам", this));
        commands.put("info", new InfoCommand("info", "вывести информацию о коллекции", this));
        commands.put("show", new ShowCommand("show", "вывести все элементы коллекции", this));
        commands.put("insert", new InsertCommand("insert", "добавить новый фильм", this));
        commands.put("update", new UpdateCommand("update", "изменить фильм по его id", this));
        commands.put("remove_key", new RemoveKeyCommand("remove_key", "удалить фильм из коллекции по его id", this));
        commands.put("clear", new ClearCommand("clear", "очистить коллекцию", this));
        commands.put("execute_script", new ExecuteScriptCommand("execute_script", "считать и исполнить скрипт из указанного файла", this));
        commands.put("remove_lower", new RemoveLowerCommand("remove_lower", "удалить из коллекции все фильмы, меньшие, чем заданный", this));
        commands.put("history", new HistoryCommand("history", "вывести последние 14 команд", this));
        commands.put("remove_lower_key", new RemoveLowerKeyCommand("remove_lower_key", "удалить из коллекции все фильмы, ключ которых меньше, чем заданный", this));
        commands.put("sum_of_budget", new SumOfBudgetCommand("sum_of_budget", "вывести сумму значений поля бюджет для всех фильмов", this));
        commands.put("count_less_than_budget", new CountLessThanBudgetCommand("count_less_than_budget", "вывести количество фильмов, бюджет которых меньше заданного", this));
        commands.put("count_greater_than_mpaa_rating", new CountGreaterThanMpaaRatingCommand("count_greater_than_mpaa_rating", "вывести количество элементов, значение поля mpaaRating которых больше заданного", this));
        commands.put("average_of_oscars_count", new AverageOfOscarsCountCommand("average_of_oscars_count", "вывести среднее значение оскаров", this));
        commands.put("filter_by_mpaa_rating", new FilterByMpaaRatingCommand("filter_by_mpaa_rating", "вывести фильмы, mpaa рейтинг которых равен заданному", this));
        commands.put("print_field_ascending_operator", new PrintFieldAscendingOperatorCommand("print_field_ascending_operator", "вывести всех операторов в порядке возрастания", this));
    }

    /**
     * Метод для получения коллекции фильмов
     *
     * @return коллекция фильмов
     */
    public Map<Integer, Movie> getMovies() {
        return movies;
    }

    /**
     * Метод для получения коллекции команд
     *
     * @return коллекция команд
     */
    public Map<String, AbstractCommand<?, ?>> getCommands() {
        return commands;
    }

    /**
     * Метод для получения списка выполненных команд над коллекцией фильмов
     *
     * @return список команд
     */
    public Queue<AbstractCommand<?, ?>> getCommandHistory(User user) {
        return commandHistory.get(user);
    }

    /**
     * Метод для выполнения команд над коллекцией
     *
     * @param commandWithArgument команда для исполнения
     * @return результат выполнения команды
     */
    public Object executeCommand(User user, CommandWithArgument commandWithArgument)
            throws FailedCommandExecutionException,
            IllegalArgumentException,
            UnsupportedOperationException {
        if (commandWithArgument.getArgument() == null)
            return executeCommand(user, commandWithArgument.getName());
        AbstractCommand<?, ?> command = commands.getOrDefault(commandWithArgument.getName(), commandNotFound);
        if (command != commandNotFound)
            pushCommandToHistory(user, command);
        return command.execute(user, commandWithArgument.getArgument());
    }

    private Object executeCommand(User user, String commandName) throws FailedCommandExecutionException {
        AbstractCommand<?, ?> command = commands.getOrDefault(commandName, commandNotFound);
        if (command != commandNotFound)
            pushCommandToHistory(user, command);
        return command.execute(user);
    }

    private void pushCommandToHistory(User user, AbstractCommand<?, ?> command) {
        if (!commandHistory.containsKey(user)) {
            commandHistory.put(user, new ArrayDeque<>());
        }
        Queue<AbstractCommand<?, ?>> userHistory = commandHistory.get(user);
        if (userHistory.size() >= 14)
            userHistory.poll();
        userHistory.add(command);
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }

    public Lock getMoviesLock() {
        return moviesLock;
    }
}
