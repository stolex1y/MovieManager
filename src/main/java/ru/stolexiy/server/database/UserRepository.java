package ru.stolexiy.server.database;

import ru.stolexiy.data.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserRepository extends AbstractRepository<String, User> {

    private final MessageDigest md;
    private static final String pepper = "*&^mVLC(#";

    private static final Map<String, User> cache = new HashMap<>();

    public UserRepository(DbConnectionManager connectionManager) {
        super(connectionManager);
        try {
            md = MessageDigest.getInstance("MD2");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
    }

    private User fromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("login"),
                rs.getString("pass")
        );
    }

    private String toInsertString(User user) {
        return new StringBuilder()
                .append("'").append(user.getLogin()).append("',")
                .append("'").append(hashPass(user.getPass())).append("'").toString();
    }

    @Override
    public User get(String key) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * " +
                    "from users where login = '" + key + "';");
            if (rs.next()) {
                User user = fromResultSet(rs);
                User cached = cache.get(key);
                if (cached != null)
                    cached.copy(user);
                else
                    cache.put(key, user);
                return user;
            } else
                return null;
        }
    }

    public boolean checkPass(User user) throws SQLException {
        String hashedPass = hashPass(user.getPass());
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * " +
                    "from users where login = '" + user.getLogin() +
                    "' and pass = '" + hashedPass + "';");
            return rs.next();
        }
    }

    public User encryptUser(User user) {
        String hashedPass = hashPass(user.getPass());
        return new User(user.getLogin(), hashedPass);
    }

    @Override
    public User insert(User value) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("insert into users values (" +
                    toInsertString(value) + ");");
            return get(value.getLogin());
        }
    }

    private String hash(String str) {
        byte[] bytes = md.digest(str.getBytes());
        BigInteger no = new BigInteger(1, bytes);
        String hashed = no.toString(16);
        while (hashed.length() < 32) {
            hashed = "0" + hashed;
        }
        return hashed;
    }

    private String hashPass(String pass) {
        String salt = hash(pass);
        return hash(pepper + pass + salt);
    }

    public static void main(String[] args) {
        UserRepository repository = new UserRepository(null);
        Scanner in = new Scanner(System.in);
        while (true) {
            String pass = in.nextLine().trim();
            System.out.println("hash for pass (" + pass + ") is (" + repository.hashPass(pass) + ")");
        }
    }

}
