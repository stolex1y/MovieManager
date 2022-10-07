package ru.stolexiy.server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.data.*;
import ru.stolexiy.server.IllegalAccessException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieRepository extends AbstractRepository<Integer, Movie> {

    private static final Logger logger = LogManager.getLogger("ServerLogger");
    private static final Map<Integer, Movie> cache = new HashMap<>();
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MpaaRatingRepository mpaaRatingRepository;


    public MovieRepository(DbConnectionManager connectionManager) {
        super(connectionManager);
        personRepository = new PersonRepository(connectionManager);
        userRepository = new UserRepository(connectionManager);
        movieGenreRepository = new MovieGenreRepository(connectionManager);
        mpaaRatingRepository = new MpaaRatingRepository(connectionManager);
        countryRepository = new CountryRepository(connectionManager);
    }

    private Movie fromResultSet(ResultSet rs) throws SQLException {
        User user = userRepository.get(rs.getString("owner"));
        Country country = countryRepository.get(rs.getInt("country"));
        MpaaRating mpaaRating = mpaaRatingRepository.get(rs.getInt("mpaaRating"));
        MovieGenre movieGenre = movieGenreRepository.get(rs.getInt("genre"));
        Person director = personRepository.get(rs.getInt("director"));
        return new Movie(
                rs.getInt("id"),
                user.getLogin(),
                rs.getString("name"),
                rs.getInt("productionYear"),
                country,
                movieGenre,
                director,
                rs.getLong("budget"),
                rs.getLong("fees"),
                mpaaRating,
                rs.getInt("durationInMinutes"),
                rs.getInt("oscarsCount"),
                rs.getTimestamp("creationDate").toLocalDateTime()
        );
    }

    private String toUpdateString(Movie movie) throws SQLException {
        return new StringBuilder()
                .append("name = '").append(movie.getName()).append("',")
                .append("productionYear = ").append(movie.getProductionYear()).append(",")
                .append("country = ").append(countryRepository.getIdByName(movie.getCountry())).append(",")
                .append("genre = ").append(movieGenreRepository.getIdByName(movie.getGenre())).append(",")
                .append("director = ").append(movie.getDirector().getId()).append(",")
                .append("budget = ").append(movie.getBudget()).append(",")
                .append("fees = ").append(movie.getFees()).append(",")
                .append("mpaaRating = ").append(mpaaRatingRepository.getIdByName(movie.getMpaaRating())).append(",")
                .append("durationInMinutes = ").append(movie.getDurationInMinutes()).append(",")
                .append("oscarsCount = ").append(movie.getOscarsCount()).toString();
    }

    private String toInsertString(Movie movie) throws SQLException {
        return new StringBuilder()
                .append("default,")
                .append("'").append(movie.getOwner()).append("',")
                .append("'").append(movie.getName()).append("',")
                .append(movie.getProductionYear()).append(",")
                .append(countryRepository.getIdByName(movie.getCountry())).append(",")
                .append(movieGenreRepository.getIdByName(movie.getGenre())).append(",")
                .append(movie.getDirector().getId()).append(",")
                .append(movie.getBudget()).append(",")
                .append(movie.getFees()).append(",")
                .append(mpaaRatingRepository.getIdByName(movie.getMpaaRating())).append(",")
                .append(movie.getDurationInMinutes()).append(",")
                .append(movie.getOscarsCount()).toString();
    }

    @Override
    public Movie get(Integer key) throws SQLException {
        logger.info("get (id " + key + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * " +
                    "from movies where id = " + key + ";");
            if (!rs.next())
                return null;
            Movie movie = fromResultSet(rs);
            Movie cached = cache.get(key);
            if (cached != null) {
                cached.copy(movie);
            } else {
                cache.put(key, movie);
            }
            return movie;
        }
    }

    public List<Movie> getAll() throws SQLException {
        logger.info("getAll");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * " +
                    "from movies;");
            List<Movie> movies = new ArrayList<>();
            while (rs.next()) {
                Movie movie = fromResultSet(rs);
                Movie cached = cache.get(movie.getId());
                if (cached != null) {
                    cached.copy(movie);
                    movies.add(cached);
                } else {
                    cache.put(movie.getId(), movie);
                    movies.add(movie);
                }
            }
            return movies;
        }
    }

    @Override
    public void update(Movie value) throws SQLException {
        logger.info("update (id " + value.getId() + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            Person person = personRepository.get(value.getDirector().getId());
            if (person == null) {
                value.setDirector(personRepository.insert(value.getDirector()));
            } else {
                personRepository.update(value.getDirector());
            }
            statement.executeUpdate("update movies set " +
                    toUpdateString(value) + " where id = " + value.getId() + ";");
        }
    }

    public void updateByUserAndId(User user, Movie value) throws SQLException, IllegalAccessException {
        if (userRepository.checkPass(user)) {
            update(value);
        } else {
            throw new IllegalAccessException();
        }
    }

    @Override
    public Movie insert(Movie value) throws SQLException {
        logger.info("insert");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            Person director = personRepository.get(value.getDirector().getId());
            if (director == null) {
                value.setDirector(personRepository.insert(value.getDirector()));
            }
            statement.executeUpdate("insert into movies values (" +
                    toInsertString(value) + ");");
            ResultSet rs = statement.executeQuery("select * from movies order by id desc limit 1");
            rs.next();
            Movie movie = fromResultSet(rs);
            cache.put(movie.getId(), movie);
            return movie;
        }
    }

    @Override
    public boolean delete(Integer key) throws SQLException {
        logger.info("delete (id " + key + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate("delete from movies where id = " + key + ";");
            cache.remove(key);
            return result > 0;
        }
    }

    public int deleteByUser(User user) throws SQLException {
        logger.info("deleteByUser (user " + user.getLogin() + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate("delete from movies where owner = '" + user.getLogin() + "';");
            cache.entrySet().removeIf(it -> it.getValue().getOwner().equals(user.getLogin()));
            return result;
        }
    }

    public boolean deleteByUserAndId(User user, Integer key) throws SQLException, IllegalAccessException {
        if (userRepository.checkPass(user)) {
            return delete(key);
        } else {
            throw new IllegalAccessException();
        }
    }

    @Override
    public int deleteAll() throws SQLException {
        logger.info("deleteAll");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate("delete from movies;");
            cache.clear();
            return result;
        }
    }
}
