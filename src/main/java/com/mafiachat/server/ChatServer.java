package com.mafiachat.server;

import static com.mafiachat.util.Constant.SERVER_PORT;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.mafiachat.server.handler.Player.Player;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;

public class ChatServer implements Runnable {
    GroupManager groupManager;
    GameManager gameManager;
    private final ServerSocket serverSocket;
    private static final Logger logger = Logger.getLogger(ChatServer.class.getSimpleName());

    public ChatServer(GroupManager groupManager, GameManager gameManager) throws IOException {
        this.groupManager = groupManager;
        this.gameManager = gameManager;
        serverSocket = new ServerSocket(SERVER_PORT);
        logger.info(
                String.format("ChatServer[%s] is listening on port %s\n", InetAddress.getLocalHost().getHostAddress(),
                        SERVER_PORT));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownHook()));
    }

    private void shutdownHook() {
        try {
            serverSocket.close();
            groupManager.closeAllMessageHandlers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Socket socket = null;
        try {
            while (true) {
                socket = serverSocket.accept();
                logger.info(String.format("Client[%s] accepted\n", socket.getInetAddress().getHostName()));
                Player player = new Player(gameManager);
                new Thread(new PlayerHandler(groupManager, gameManager, socket, player)).start();
            }
        } catch (IOException e) {
            logger.severe("Terminating ChatServer: " + e.getMessage());
        }
        logger.info("ChatServer shut down");
    }

    public void cleanup() throws IOException {
        serverSocket.close();
        groupManager.closeAllMessageHandlers();
    }

    public static void main(String[] args) {
        try {
            Runnable r = new ChatServer(GroupManager.getInstance(), GameManager.getInstance());
            new Thread(r).start();
        } catch (IOException e) {
            logger.severe("Failed to start server: " + e.getMessage());
        }
    }
}
