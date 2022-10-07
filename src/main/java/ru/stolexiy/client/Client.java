package ru.stolexiy.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.client.console.IO;
import ru.stolexiy.client.exceptions.InternalClientException;
import ru.stolexiy.client.exceptions.InternalServerException;
import ru.stolexiy.client.network.UserRepository;
import ru.stolexiy.client.network.NetworkConnection;
import ru.stolexiy.connection.ResponseType;
import ru.stolexiy.connection.ServerMessage;
import ru.stolexiy.data.User;
import ru.stolexiy.exceptions.InterruptionOfCommandException;

import java.net.*;
import java.util.NoSuchElementException;

/**
 * Класс, который взаимодействует с клиентом
 */
public class Client {
    private static final Logger logger = LogManager.getLogger("ClientLogger");
    private static int serverPort = 2406;
    private final IO io;
    private final NetworkConnection connection;
    private final CommandManager commandManager;
    private final UserRepository authentication;

    /**
     * Конструктор с заданным модулем ввода/вывода
     *
     * @param io            объект, представляющий модуль ввода/вывода
     * @param remoteAddress адрес сервера
     * @throws InternalClientException если не удалось создать объект клиента
     */
    public Client(IO io, InetSocketAddress remoteAddress) throws InternalClientException {
        this.io = io;
        this.commandManager = new CommandManager(io);
        if (remoteAddress != null)
            connection = NetworkConnection.getInstance();
        else
            connection = null;
        this.authentication = new UserRepository();
    }

    /**
     * Конструктор для создания клиента без соединения к серверу
     */
    public Client(IO io) throws InternalClientException {
        this(io, null);
    }

    private User authenticate() throws InternalServerException, InternalClientException {
        User user = null;
        while (user == null) {
            String authCmd = "";
            try {
                authCmd = io.chooseCommand("signup", "signin", "exit");
            } catch (NoSuchElementException e) {
                commandManager.exit();
            }
            if (authCmd.equals("exit"))
                commandManager.exit();
            user = IO.readUserDataFromConsole();
            if (authCmd.equals("signin")) {
                user = authentication.userSignIn(user);
                if (user == null)
                    io.printString("Неверный логин или пароль");
            } else if (authCmd.equals("signup")) {
                user = authentication.userSignUp(user);
                if (user == null)
                    io.printString("Данное имя пользователя уже используется");
            }
        }
        return user;
    }


    private static String handleCommandResult(ServerMessage serverMessage) throws InternalClientException {
        ResponseType type = serverMessage.getType();
        switch (type) {
            case OK:
            case FAILED_EXECUTION:
                return serverMessage.getMessageBody().toString();
            case ILLEGAL_ACCESS:
                return "Недостаточно прав.";
            default:
                logger.error("Этого тут быть не должно " + type);
                throw new InternalClientException();
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static void main(String[] args) {
        if (args.length != 0)
            serverPort = Integer.parseInt(args[0]);
        try {
            InetSocketAddress serverAddr = new InetSocketAddress(InetAddress.getLocalHost(), serverPort);
            Client client = new Client(new IO(System.in, System.out), serverAddr);
            User user = client.authenticate();
            String commandName = client.commandManager.readCommand();
            while (commandName != null) {
                try {
                    Object commandArg = client.commandManager.readCommandArg(commandName);
                    ServerMessage receivedMessage = client.connection.sendCommand(user, commandName, commandArg);
                    client.io.printString(handleCommandResult(receivedMessage));
                } catch (InterruptionOfCommandException e) {
                    if (e.getMessage() != null && !e.getMessage().equals(""))
                        client.io.printString(e.getMessage());
                    client.io.printString("Команда прервана.");
                }
                commandName = client.commandManager.readCommand();
            }
        } catch (InternalServerException e) {
            logger.error("Ошибка сервера", e);
            System.out.println(e.getMessage());
        } catch (InternalClientException | UnknownHostException e) {
            logger.error("Ошибка клиента", e);
            System.out.println(e.getMessage());
        }
    }
}
