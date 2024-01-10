package com.mafiachat.server;

import static com.mafiachat.util.Constant.SERVER_HOST;
import static com.mafiachat.util.Constant.SERVER_PORT;

import com.mafiachat.client.util.ImageProvider;
import com.mafiachat.server.handler.Player.Player;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ChatServer implements Runnable {
    private final GroupManager groupManager;
    private final GameManager gameManager;
    private final ServerSocket serverSocket;
    private static final Logger logger = Logger.getLogger(ChatServer.class.getSimpleName());
    private static String host = SERVER_HOST;
    private static int port = SERVER_PORT;

    public ChatServer(GroupManager groupManager, GameManager gameManager) throws IOException {
        this.groupManager = groupManager;
        this.gameManager = gameManager;
        serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host));
        logger.info(
                String.format("ChatServer[%s] is listening on port %s\n", InetAddress.getLocalHost().getHostAddress(),
                        SERVER_PORT));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownHook()));
    }

    public void shutdownHook() {
        try {
            serverSocket.close();
            groupManager.closeAllClientHandlers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
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
        groupManager.closeAllClientHandlers();
    }

    public static void main(String[] args) {
        setHostAndPortUsingDialog();
        while (!tryRunServer()) {
            setHostAndPortUsingDialog();
        }
        JFrame serverWindow = new JFrame("MafiaStart");
        serverWindow.setSize(500, 400);
        serverWindow.setVisible(true);
        serverWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon mafiaIcon = ImageProvider.getInstance().getMafiaIcon();
        serverWindow.setIconImage(mafiaIcon.getImage());
    }

    private static boolean tryRunServer() {
        boolean isRunning = false;
        try {
            Runnable r = new ChatServer(GroupManager.getInstance(), GameManager.getInstance());
            new Thread(r).start();
            isRunning = true;
        } catch (IOException e) {
            logger.severe("Failed to start server: " + e.getMessage());
        }
        return isRunning;
    }

    private static void setHostAndPortUsingDialog() {
        try {
            host = JOptionPane.showInputDialog("접속 호스트를 입력하세요.");
            port = Integer.parseInt(JOptionPane.showInputDialog("접속 포트를 입력하세요."));
        } catch (NumberFormatException e) {
            logger.severe("Invalid input: " + e.getMessage());
        }
    }
}
