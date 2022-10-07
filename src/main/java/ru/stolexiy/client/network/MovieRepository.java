package ru.stolexiy.client.network;

import ru.stolexiy.client.exceptions.InternalClientException;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.connection.ServerMessage;
import ru.stolexiy.data.Country;
import ru.stolexiy.data.Movie;
import ru.stolexiy.data.Person;
import ru.stolexiy.data.User;

import java.util.List;

public class MovieRepository {
    private static final NetworkConnection networkConnection = NetworkConnection.getInstance();

    public static List<Movie> getAll(User user) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "show", null);
        if (response.isSuccess()) {
            return (List<Movie>) response.getMessageBody();
        } else {
            throw new InternalClientException();
        }
    }

    public static boolean removeById(User user, int id) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "remove_key", id);
        return response.isSuccess();
    }

    public static void add(User user, Movie movie) throws InternalServerException, InternalClientException {
        networkConnection.sendCommand(user, "insert", movie);
    }

    public static boolean update(User user, Movie movie) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "update", movie);
        return response.isSuccess();
    }

    public static List<Person> getAllDirectorsAsc(User user) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "print_field_ascending_operator", null);
        if (response.isSuccess()) {
            return (List<Person>) response.getMessageBody();
        } else {
            throw new InternalClientException();
        }
    }

    public static void removeLowerThanId(User user, int id) throws InternalServerException, InternalClientException {
        networkConnection.sendCommand(user, "remove_lower_key", id);
    }

    public static void clearUserMovies(User user) throws InternalServerException, InternalClientException {
        networkConnection.sendCommand(user, "clear", null);
    }

    public static double getAvgOscarsCount(User user) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "average_of_oscars_count", null);
        return (double) response.getMessageBody();
    }

    public static long getAllBudgetsSum(User user) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "sum_of_budget", null);
        return (long) response.getMessageBody();
    }

    public static long getCountByCountry(User user, Country country) throws InternalServerException, InternalClientException {
        List<Movie> movies = getAll(user);
        return movies.stream().filter(movie -> movie.getCountry() == country).count();
    }

    public static long getCountByYear(User user, int productionYear) throws InternalServerException, InternalClientException {
        List<Movie> movies = getAll(user);
        return movies.stream().filter(movie -> movie.getProductionYear() == productionYear).count();
    }

    public static long getCountLowerBudget(User user, long budget) throws InternalServerException, InternalClientException {
        List<Movie> movies = getAll(user);
        return movies.stream().filter(movie -> movie.getBudget() < budget).count();
    }

    public static String getCollectionInfo(User user) throws InternalServerException, InternalClientException {
        ServerMessage response = networkConnection.sendCommand(user, "info", null);
        return (String) response.getMessageBody();
    }


}
