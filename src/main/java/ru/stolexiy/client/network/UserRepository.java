package ru.stolexiy.client.network;

import ru.stolexiy.client.exceptions.InternalClientException;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.connection.ServerMessage;
import ru.stolexiy.data.User;


public class UserRepository {
    private static final NetworkConnection connection = NetworkConnection.getInstance();

    public static User userSignIn(User user) throws InternalClientException, InternalServerException {
        ServerMessage serverMessage = connection.signIn(user);
        if (serverMessage.isSuccess())
            return (User) serverMessage.getMessageBody();
        else
            return null;
    }

    public static User userSignUp(User user) throws InternalServerException, InternalClientException {
        ServerMessage serverMessage = connection.signUp(user);
        if (serverMessage.isSuccess())
            return (User) serverMessage.getMessageBody();
        else
            return null;
    }
}
