package ru.stolexiy.server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbConnectionManager {
    private static final Logger logger = LogManager.getLogger("ServerLogger");

    private PooledConnection pooledConnection;

    public DbConnectionManager() {
        Properties properties = new Properties();
        PGConnectionPoolDataSource connectionPoolDataSource = new PGConnectionPoolDataSource();
        try (InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("database.properties")) {
            properties.load(input);
            connectionPoolDataSource.setUrl(properties.getProperty("jdbc.url"));
            connectionPoolDataSource.setUser(properties.getProperty("jdbc.login"));
            connectionPoolDataSource.setPassword(properties.getProperty("jdbc.pass"));
            pooledConnection = connectionPoolDataSource.getPooledConnection();
        } catch (IOException e) {
            logger.error("Не удалось прочитать database.properties", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return pooledConnection.getConnection();
    }

    public static void main(String[] args) throws SQLException {
        DbConnectionManager manager = new DbConnectionManager();
        try (Connection connection = manager.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * from countries");
            while (rs.next()) {
                System.out.print(rs.getString("id") + ", ");
                System.out.println(rs.getString("name"));
            }
        }
    }
}
