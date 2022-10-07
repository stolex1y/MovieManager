package ru.stolexiy.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractRepository<K, V> {
    protected final DbConnectionManager connectionManager;

    public V get(K key) throws SQLException {
        throw new UnsupportedOperationException();
    }

    protected Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public AbstractRepository(DbConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public List<V> getAll() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public V insert(V value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void update(V value) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean delete(K key) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int deleteAll() throws SQLException {
        throw new UnsupportedOperationException();
    }
}
