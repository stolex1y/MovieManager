package ru.stolexiy.server.database;

import ru.stolexiy.data.Country;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CountryRepository extends AbstractRepository<Integer, Country> {

    public CountryRepository(DbConnectionManager connectionManager) {
        super(connectionManager);
    }

    private Country fromResultSet(ResultSet rs) throws SQLException {
        return Country.valueOf(rs.getString("name").toUpperCase());
    }

    @Override
    public Country get(Integer key) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name " +
                    "from countries where id = " + key + ";");
            if (rs.next()) {
                return fromResultSet(rs);
            } else
                return null;
        }
    }

    @Override
    public List<Country> getAll() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name " +
                    "from countries;");
            List<Country> countries = new ArrayList<>();
            while (rs.next()) {
                countries.add(fromResultSet(rs));
            }
            return countries;
        }
    }

    public Integer getIdByName(Country country) throws SQLException {
        if (country == null)
            return null;
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select id " +
                    "from countries where name = '" + country.name() + "';");
            if (rs.next()) {
                return rs.getInt("id");
            } else
                return null;
        }
    }
}
