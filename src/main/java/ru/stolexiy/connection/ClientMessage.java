package ru.stolexiy.connection;

import ru.stolexiy.data.User;

import java.io.Serializable;
import java.net.SocketAddress;

public class ClientMessage implements Serializable {
    private final User user;
    private final RequestType type;
    private final Object messageBody;
    private final SocketAddress senderAddress;

    public ClientMessage(User user, RequestType type, Object messageBody, SocketAddress senderAddress) {
        this.user = user;
        this.type = type;
        this.messageBody = messageBody;
        this.senderAddress = senderAddress;
    }

    public RequestType getType() {
        return type;
    }

    public Object getMessageBody() {
        return messageBody;
    }

    public SocketAddress getSenderAddress() {
        return senderAddress;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "user=" + user +
                ", type=" + type +
                ", messageBody=" + messageBody +
                ", senderAddress=" + senderAddress +
                '}';
    }
}
