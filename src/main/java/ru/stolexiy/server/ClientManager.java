package ru.stolexiy.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.connection.ClientMessage;
import ru.stolexiy.connection.CommandWithArgument;
import ru.stolexiy.connection.ResponseType;
import ru.stolexiy.connection.ServerMessage;
import ru.stolexiy.data.User;
import ru.stolexiy.server.database.UserRepository;

import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Модуль обработки запросов клиента {@link ClientMessage} и подготовки ответов сервера {@link ServerMessage}.
 */
public class ClientManager {
    private static final Logger logger = LogManager.getLogger("ServerLogger");
    private final MovieManager movieManager;

    private final UserRepository userRepository;
    private final ExecutorService threadPool;

    /**
     * @param movieManager менеджер для управления коллекцией фильмов
     */
    public ClientManager(UserRepository userRepository, MovieManager movieManager) {
        this.movieManager = movieManager;
        this.userRepository = userRepository;
        this.threadPool = Executors.newFixedThreadPool(2);
    }

    /*
        Обработка запросов, связанных с непосредственным выполнением команд над коллекцией.
        throws FailedCommandExecutionException, если выполнение команды завершилось неуспешно.
     */
    private Object executeCommand(User user, CommandWithArgument commandWithArgument)
            throws FailedCommandExecutionException,
            UnsupportedOperationException,
            IllegalArgumentException {
        return movieManager.executeCommand(user, commandWithArgument);
    }

    /**
     * Обработчик клиентского сообщения.
     *
     * @param receivedMessage сообщение клиента
     * @return ответ сервера на запрос клиента
     */
    public Future<ServerMessage> handleClientMessage(ClientMessage receivedMessage) {
        return threadPool.submit(() -> {
            ServerMessage sendingMessage = null;
            Object messageBody = receivedMessage.getMessageBody();
            SocketAddress destination = receivedMessage.getSenderAddress();
            try {
                switch (receivedMessage.getType()) {
                    case SIGN_IN:
                        User authUser = (User) messageBody;
                        logger.info("Запрос на аутентификацию от пользователя " + authUser.getLogin());
                        if (userRepository.checkPass(authUser))
                            sendingMessage = new ServerMessage(
                                    userRepository.encryptUser(authUser),
                                    ResponseType.OK,
                                    destination
                            );
                        else
                            sendingMessage = new ServerMessage(ResponseType.LOGIN_FAILED, destination);
                        break;
                    case SIGN_UP:
                        User registerUser = (User) messageBody;
                        logger.info("Запрос на регистрацию от пользователя: " + registerUser.getLogin());
                        if (userRepository.get(registerUser.getLogin()) != null)
                            sendingMessage = new ServerMessage(ResponseType.LOGIN_FAILED, destination);
                        else {
                            userRepository.insert(registerUser);
                            sendingMessage = new ServerMessage(
                                    userRepository.encryptUser(registerUser),
                                    ResponseType.OK,
                                    destination
                            );
                        }
                        break;
                    case COMMAND:
                        try {
                            if (receivedMessage.getUser() == null)
                                throw new IllegalArgumentException();
                            logger.info("Попытка выполнить команду(" + messageBody + ") от пользователя " +
                                    receivedMessage.getUser().getLogin());
                            sendingMessage = new ServerMessage(
                                    executeCommand(
                                            receivedMessage.getUser(),
                                            (CommandWithArgument) messageBody
                                    ),
                                    ResponseType.OK,
                                    destination
                            );
                        } catch (FailedCommandExecutionException e) {
                            logger.info("Неуспешное выполнение команды");
                            Throwable cause = e.getCause();
                            if (cause instanceof SQLException) {
                                throw (SQLException) cause;
                            } else if (cause instanceof IllegalAccessException) {
                                sendingMessage = new ServerMessage(ResponseType.ILLEGAL_ACCESS, destination);
                            } else
                                sendingMessage = new ServerMessage(e.getMessage(), ResponseType.FAILED_EXECUTION, destination);
                        } catch (IllegalArgumentException | UnsupportedOperationException e) {
                            logger.warn("Получен некорректный запрос на выполнение команды: " + messageBody);
                            sendingMessage = new ServerMessage(ResponseType.INVALID_REQUEST, destination);
                        }
                        break;
                    default:
                        logger.error("Необработанный тип запроса: " + receivedMessage);
                        sendingMessage = new ServerMessage(ResponseType.INTERNAL_SERVER_ERROR, destination);
                        break;
                }
            } catch (ClassCastException e) {
                logger.warn("Получен некорректный запрос на выполнение команды: " + messageBody);
                sendingMessage = new ServerMessage(ResponseType.INVALID_REQUEST, destination);
            } catch (SQLException e) {
                logger.warn("Ошибка в базе данных", e);
                sendingMessage = new ServerMessage(ResponseType.INTERNAL_SERVER_ERROR, destination);
            } finally {
                if (sendingMessage != null) {
                    if (receivedMessage.getUser() != null)
                        logger.info("Запрос выполнен, сформированный ответ " + sendingMessage.getType() +
                                " для пользователя " + receivedMessage.getUser().getLogin());
                    else
                        logger.info("Запрос выполнен, сформированный ответ " + sendingMessage.getType());
                }
            }
            return sendingMessage;
        });
    }

}
