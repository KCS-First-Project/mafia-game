package com.mafiachat.server;

import static com.mafiachat.util.Constant.SERVER_PORT;

import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GroupManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ChatServer implements Runnable {
    private final ServerSocket serverSocket;
    private static final Logger logger = Logger.getLogger(ChatServer.class.getSimpleName());

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        logger.info(
                String.format("ChatServer[%s] is listening on port %s\n", InetAddress.getLocalHost().getHostAddress(),
                        SERVER_PORT));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownHook()));
    }

    public void shutdownHook() {
        try {
            serverSocket.close();
            GroupManager.closeAllMessageHandlers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info(String.format("Client[%s] accepted\n", socket.getInetAddress().getHostName()));
                new Thread(new PlayerHandler(socket)).start();
            }
        } catch (IOException e) {
            logger.severe("Terminating ChatServer: " + e.getMessage());
        }
        logger.info("ChatServer shut down");
    }

    public void cleanup() throws IOException {
        serverSocket.close();
        GroupManager.closeAllMessageHandlers();
    }

    public static void main(String[] args) {
        try {
            Runnable r = new ChatServer();
            new Thread(r).start();
        } catch (IOException e) {
            logger.severe("Failed to start server: " + e.getMessage());
        }
    }
}
