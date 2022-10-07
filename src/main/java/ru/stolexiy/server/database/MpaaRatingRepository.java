package ru.stolexiy.server.database;

import ru.stolexiy.data.MpaaRating;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MpaaRatingRepository extends AbstractRepository<Integer, MpaaRating> {

    public MpaaRatingRepository(DbConnectionManager connectionManager) {
        super(connectionManager);
    }

    private MpaaRating fromResultSet(ResultSet rs) throws SQLException {
        return MpaaRating.valueOf(rs.getString("name").toUpperCase());
    }

    @Override
    public MpaaRating get(Integer key) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name " +
                    "from mpaaratings where id = " + key + ";");
            if (rs.next()) {
                return fromResultSet(rs);
            } else
                return null;
        }
    }

    @Override
    public List<MpaaRating> getAll() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name " +
                    "from mpaaratings;");
            List<MpaaRating> ratings = new ArrayList<>();
            while (rs.next()) {
                ratings.add(fromResultSet(rs));
            }
            return ratings;
        }
    }

    public Integer getIdByName(MpaaRating mpaaRating) throws SQLException {
        if (mpaaRating == null)
            return null;
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select id " +
                    "from mpaaratings where name = '" + mpaaRating.name() + "';");
            if (rs.next()) {
                return rs.getInt("id");
            } else
                return null;
        }
    }
}
