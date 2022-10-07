package ru.stolexiy.server.database;

import ru.stolexiy.data.MovieGenre;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovieGenreRepository extends AbstractRepository<Integer, MovieGenre> {
    public MovieGenreRepository(DbConnectionManager connectionManager) {
        super(connectionManager);
    }

    private MovieGenre fromResultSet(ResultSet rs) throws SQLException {
        return MovieGenre.valueOf(rs.getString("name").toUpperCase());
    }

    @Override
    public MovieGenre get(Integer key) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name " +
                    "from moviegenres where id = " + key + ";");
            if (rs.next()) {
                return fromResultSet(rs);
            } else
                return null;
        }
    }

    @Override
    public List<MovieGenre> getAll() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name " +
                    "from moviegenres;");
            List<MovieGenre> genres = new ArrayList<>();
            while (rs.next()) {
                genres.add(fromResultSet(rs));
            }
            return genres;
        }
    }

    public Integer getIdByName(MovieGenre movieGenre) throws SQLException {
        if (movieGenre == null)
            return null;
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select id " +
                    "from moviegenres where name = '" + movieGenre.name() + "';");
            if (rs.next()) {
                return rs.getInt("id");
            } else
                return null;
        }
    }
}
