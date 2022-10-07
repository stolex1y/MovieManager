package ru.stolexiy.server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.data.Country;
import ru.stolexiy.data.Person;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PersonRepository extends AbstractRepository<Integer, Person> {

    private static final Logger logger = LogManager.getLogger("ServerLogger");
    private static final Map<Integer, Person> cache = new HashMap<>();
    private final CountryRepository countryRepository;

    public PersonRepository(DbConnectionManager connectionManager) {
        super(connectionManager);
        countryRepository = new CountryRepository(connectionManager);
    }

    private Person fromResultSet(ResultSet rs) throws SQLException {
        Country nationality = countryRepository.get(rs.getInt("nationality"));
        return new Person(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("growthInMetres"),
                nationality,
                rs.getInt("filmCount")
        );
    }

    private String toUpdateString(Person person) throws SQLException {
        return new StringBuilder()
                .append("name = '").append(person.getName()).append("',")
                .append("growthInMetres = ").append(person.getGrowthInMetres()).append(",")
                .append("nationality = ").append(countryRepository.getIdByName(person.getNationality())).append(",")
                .append("filmCount = ").append(person.getFilmCount()).toString();
    }

    private String toInsertString(Person person) throws SQLException {
        return new StringBuilder()
                .append("default,")
                .append("'").append(person.getName()).append("',")
                .append(person.getGrowthInMetres()).append(",")
                .append(countryRepository.getIdByName(person.getNationality())).append(",")
                .append(person.getFilmCount()).toString();
    }

    @Override
    public Person get(Integer key) throws SQLException {
        logger.info("get (id " + key + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * " +
                    "from persons where id = " + key + ";");
            if (rs.next()) {
                Person person = fromResultSet(rs);
                Person cached = cache.get(key);
                if (cached != null) {
                    cached.copy(person);
                    return cached;
                } else {
                    cache.put(key, person);
                    return person;
                }
            } else
                return null;
        }
    }

    @Override
    public void update(Person value) throws SQLException {
        logger.info("update (id " + value.getId() + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("update persons set " +
                    toUpdateString(value) + " where id = " + value.getId() + ";");
        }
    }

    @Override
    public Person insert(Person value) throws SQLException {
        logger.info("insert");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("insert into persons values (" +
                    toInsertString(value) + ");");
            ResultSet rs = statement.executeQuery("select * from persons order by id desc limit 1");
            rs.next();
            Person person = fromResultSet(rs);
            cache.put(person.getId(), person);
            return person;
        }
    }

    @Override
    public boolean delete(Integer key) throws SQLException {
        logger.info("delete (id " + key + ")");
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate("delete from persons where id = " + key + ";");
            cache.remove(key);
            return result > 0;
        }
    }
}
