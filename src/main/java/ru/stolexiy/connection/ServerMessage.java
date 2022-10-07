package ru.stolexiy.connection;

import java.io.Serializable;
import java.net.SocketAddress;

public class ServerMessage implements Serializable {
    private final Object messageBody;
    private final SocketAddress destination;
    private final ResponseType type;

    public ServerMessage(Object messageBody, ResponseType type, SocketAddress destination) {
        this.messageBody = messageBody;
        this.type = type;
        this.destination = destination;
    }

    public ServerMessage(ResponseType type, SocketAddress destination) {
        this(null, type, destination);
    }

    public Object getMessageBody() {
        return messageBody;
    }

    public SocketAddress getDestination() {
        return destination;
    }

    public ResponseType getType() {
        return type;
    }

    public boolean isSuccess() {
        return type == ResponseType.OK;
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
                "messageBody=" + messageBody +
                ", destination=" + destination +
                ", type=" + type +
                '}';
    }
}
