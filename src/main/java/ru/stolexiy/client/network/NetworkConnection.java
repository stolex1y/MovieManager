package ru.stolexiy.client.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.client.exceptions.InternalClientException;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.connection.*;
import ru.stolexiy.data.User;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class NetworkConnection {
    private static final Logger logger = LogManager.getLogger("ClientLogger");
    private final DatagramSocket socket;
    private final InetSocketAddress remoteAddr;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
    private final SocketAddress localAddr;

    private static final NetworkConnection instance;

    static {
        try {
            instance = new NetworkConnection(new InetSocketAddress(InetAddress.getLoopbackAddress(), 2406));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetworkConnection getInstance() {
        return instance;
    }

    private NetworkConnection(InetSocketAddress remoteAddr) throws IOException {
        this.remoteAddr = remoteAddr;
        socket = new DatagramSocket();
        localAddr = new InetSocketAddress(InetAddress.getLocalHost(), socket.getLocalPort());
        socket.setSoTimeout(5000);
    }

    private void sendMessage(User user, RequestType type, Object object) throws InternalClientException {
        ClientMessage message = new ClientMessage(user, type, object, localAddr);
        logger.info("Попытка отправить сообщение серверу " + type);
        try {
            try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(buffer.capacity());
                 ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
                objOut.writeObject(message);
                objOut.flush();
                buffer.clear();
                buffer.put(byteOut.toByteArray());
                buffer.flip();
                DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.limit(), remoteAddr);
                socket.send(packet);
            }
        } catch (IOException e) {
            logger.error("Не удалось отправить сообщение", e);
            throw new InternalClientException(e);
        }
    }

    public synchronized ServerMessage sendCommand(User user, String commandName, Object commandArg) throws InternalClientException, InternalServerException {
        sendMessage(user, RequestType.COMMAND, new CommandWithArgument(commandName, commandArg));
        return receiveMessage();
    }

    public ServerMessage signIn(User user) throws InternalClientException, InternalServerException {
        sendMessage(null, RequestType.SIGN_IN, user);
        return receiveMessage();
    }

    public ServerMessage signUp(User user) throws InternalClientException, InternalServerException {
        sendMessage(null, RequestType.SIGN_UP, user);
        return receiveMessage();
    }

    public ServerMessage receiveMessage() throws InternalClientException, InternalServerException {
        try {
            buffer.clear();
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.capacity(), remoteAddr);
            socket.receive(packet);
            buffer.limit(packet.getLength());
            try (ObjectInputStream objIn = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.array(), 0, buffer.limit())
            )) {
                ServerMessage received = (ServerMessage) objIn.readObject();
                checkServerMessage(received);
                logger.info("Получил сообщение от сервера " + received.getType());
                return received;
            }
        } catch (SocketTimeoutException e) {
            throw new InternalServerException("Сервер недоступен.");
        } catch (ClassNotFoundException | ClassCastException | IOException e) {
            logger.error("Ошибка при получении сообщения от сервера", e);
            throw new InternalClientException();
        }
    }

    private static void checkServerMessage(ServerMessage serverMessage) throws InternalServerException, InternalClientException {
        ResponseType type = serverMessage.getType();
        switch (type) {
            case NOT_AVAILABLE:
            case INTERNAL_SERVER_ERROR:
                throw new InternalServerException();
            case INVALID_REQUEST:
                throw new InternalClientException();
        }
    }

}
