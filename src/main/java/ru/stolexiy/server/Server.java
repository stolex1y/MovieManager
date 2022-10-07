package ru.stolexiy.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.stolexiy.connection.ClientMessage;
import ru.stolexiy.server.database.DbConnectionManager;
import ru.stolexiy.server.database.MovieRepository;
import ru.stolexiy.server.database.UserRepository;

import java.io.*;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Класс, определяющий серверное приложение, который объединяет
 * все необходимые модули для выполнения команд над коллекцией фильмов
 */
public class Server {
    private static int port = 2406;
    private static final Logger logger = LogManager.getLogger("ServerLogger");
    private final MovieManager movieManager;
    private final ClientManager clientManager;
    private final NetworkConnection networkConnection;
    private final DbConnectionManager dbConnectionManager;

    public Server(int port) throws SocketException, SQLException {
        this.dbConnectionManager = new DbConnectionManager();
        this.movieManager = new MovieManager(
                new MovieRepository(dbConnectionManager)
        );
        this.networkConnection = new NetworkConnection(port);
        this.clientManager = new ClientManager(
                new UserRepository(dbConnectionManager),
                movieManager
        );
        logger.info("Инициализация сервера");
    }

    private void executeCommand(String cmd) {
        if (cmd == null || cmd.equals(""))
            return;
        if ("exit".equals(cmd)) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        if (args.length != 0)
            Server.port = Integer.parseInt(args[0]);
        Server server;
        try {
            server = new Server(Server.port);
            System.out.println("Server's running");
        } catch (SocketException | SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                ClientMessage receivedMessage = server.networkConnection.receiveMessage();
                if (receivedMessage != null) {
                    new Thread(() -> {
                        try {
                            server.networkConnection.sendMessage(
                                    server.clientManager.handleClientMessage(receivedMessage).get()
                            );
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

                }
                if (reader.ready()) {
                    server.executeCommand(reader.readLine().trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


