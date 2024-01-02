package main.java.com.mafiachat.server.handler;


import static main.java.com.mafiachat.util.Constant.BASIC_CHAT_NAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;
import main.java.com.mafiachat.protocol.ChatRequest;
import main.java.com.mafiachat.protocol.Command;
import main.java.com.mafiachat.server.Role;
import main.java.com.mafiachat.server.handler.Player.Player;
import main.java.com.mafiachat.server.handler.Player.PlayerCommand;
import main.java.com.mafiachat.server.manager.GameManager;
import main.java.com.mafiachat.server.manager.GroupManager;

public class PlayerHandler implements Runnable, ClientHandler {
    private final Socket socket;
    private final BufferedReader br;
    private final PrintWriter pw;
    private final String host;
    private final int id;
    private String chatName;
    private boolean ready = false;
    private boolean alive = true;
    private Role role;
    private Player player;
    private static final Logger logger = Logger.getLogger(PlayerHandler.class.getSimpleName());

    public PlayerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.host = this.socket.getInetAddress().getHostAddress();
        this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.pw = new PrintWriter(this.socket.getOutputStream(), true);
        this.id = GroupManager.createClientId();
        this.chatName = BASIC_CHAT_NAME;
        this.player = new PlayerCommand();
        GroupManager.addClientHandler(this);
        GameManager.addPlayerHandler(this);
    }

    public void run() {
        try {
            while (true) {
                ChatRequest request = this.getRequest();
                if (request == null) {
                    break;
                }
                processRequest(request);
                logger.info("lineRead: " + request.getFormattedMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            GroupManager.removeClientHandler(this);
            GameManager.removePlayerHandler(this);
            close();
        }
        logger.info("Terminating ClientHandler");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getClientName() {
        return this.chatName;
    }

    @Override
    public String getFrom() {
        return this.host;
    }

    @Override
    public void sendMessage(String message) {
        this.pw.println(message);
    }

    @Override
    public ChatRequest getRequest() throws IOException {
        String formattedMessage = this.br.readLine();
        return new ChatRequest(formattedMessage);
    }

    @Override
    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processRequest(ChatRequest request) {
        Command command = request.getCommand();
        switch (command) {
            case NORMAL:
                player.sendNormalMessage(request, role);
                break;
            case INIT_ALIAS:
                player.initAlias(chatName, request, this);
                break;
            case READY:
                setReady();
                GameManager.tryStartGame();
                break;
            case VOTE:
                player.vote(request);
                break;
            case ACT_ROLE:
                player.targetPlayer(request, this);
                break;
            default:
                logger.info(String.format("ChatCommand %s \n", command.name()));
        }
    }


    public Role getRole() {
        return this.role;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setReady() {
        this.ready = true;
    }

    public void killInGame() {
        this.alive = false;
    }


}
