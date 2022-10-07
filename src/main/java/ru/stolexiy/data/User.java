package ru.stolexiy.data;

import ru.stolexiy.client.exceptions.InvalidInputValueException;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public class User implements Serializable {
    private static final Pattern loginPattern = Pattern.compile("[\\d\\w.\\-_]+");
    private static final Pattern passPattern = Pattern.compile("[^\\\\s]{5,64}");
    private String login;
    private String pass;

    public User() {
        login = "";
        pass = "";
    }

    public User(String login, String pass) throws InvalidInputValueException {
        setLogin(login);
        setPass(pass);
    }

    public void setLogin(String login) throws InvalidInputValueException {
        if (!Pattern.matches(loginPattern.pattern(), login))
            throw new InvalidInputValueException("login");
        this.login = login;
    }

    public void setPass(String pass) throws InvalidInputValueException {
        if (!Pattern.matches(passPattern.pattern(), pass))
            throw new InvalidInputValueException("pass");
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '}';
    }

    public void copy(User user) {
        this.login = user.getLogin();
        this.pass = user.getPass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(pass, user.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, pass);
    }
}
