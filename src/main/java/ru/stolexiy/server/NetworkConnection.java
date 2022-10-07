package ru.stolexiy.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.connection.ClientMessage;
import ru.stolexiy.connection.ResponseType;
import ru.stolexiy.connection.ServerMessage;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Модуль для получения сообщений ({@link ClientMessage} от клиентов
 * и отправки сообщений в ответ ({@link ServerMessage})
 */
public class NetworkConnection {
    private static final Logger logger = LogManager.getLogger("ServerLogger");
    private final ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
    private final DatagramChannel channel;

    /**
     * @param port порт, который будет прослушиваться на наличие входящих запросов
     * @throws SocketException если по каким-то причинам не удалось открыть, например, порт уже используется
     */
    public NetworkConnection(int port) throws SocketException {
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.configureBlocking(false);
        } catch (BindException e) {
            logger.error(e);
            throw new SocketException("Данный порт уже используется.");
        } catch (IOException e) {
            logger.error(e);
            throw new SocketException("Не удалось открыть соединение.");
        }
    }

    /**
     * Принять сообщение, если есть (неблокирующий режим), если запросов нет, то возвращается null.
     *
     * @return полученное сообщение или null, если запросов в данный момент нет.
     */
    public ClientMessage receiveMessage() {
        synchronized (buffer) {
            buffer.clear();
            SocketAddress destination = null;
            try {
                destination = channel.receive(buffer);
                buffer.flip();
                if (buffer.limit() == 0)
                    return null;
                logger.info("Получен запрос от клиента");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
                try (ObjectInputStream objIn = new ObjectInputStream(inputStream)) {
                    ClientMessage receivedMsg = (ClientMessage) objIn.readObject();
                    if (receivedMsg.getUser() != null)
                        logger.info("Сообщение принято от пользователя " +
                                receivedMsg.getUser().getLogin() + " следующего типа: " +
                                receivedMsg.getType());
                    else
                        logger.info("Сообщение принято следующего типа: " +
                                receivedMsg.getType());
                    return receivedMsg;
                }
            } catch (InvalidClassException | ClassNotFoundException | ClassCastException e) {
                if (destination != null)
                    sendMessage(new ServerMessage(ResponseType.INVALID_REQUEST, destination));
                logger.warn("Неизвестный запрос", e);
                return null;
            } catch (IOException e) {
                logger.error("Ошибка при получении запроса", e);
                return null;
            }
        }
    }

    /**
     * Отправить сообщение по адресу, содержащемуся в аргументе.
     *
     * @param message сообщение для отправки
     */
    public void sendMessage(ServerMessage message) {
        synchronized (buffer) {
            if (message == null)
                return;
            try {
                try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(buffer.capacity());
                     ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
                    objOut.writeObject(message);
                    objOut.flush();
                    buffer.clear();
                    buffer.put(byteOut.toByteArray());
                    buffer.flip();
                    channel.send(buffer, message.getDestination());
                }
            } catch (IOException e) {
                logger.error("Не удалось отправить сообщение", e);
            }
        }
    }
}
