package com.mafiachat.server.handler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.Player.Playable;
import com.mafiachat.server.handler.Player.Player;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;

public class PlayerHandler implements Runnable, ClientHandler {
    private final Socket socket;
    private final BufferedReader br;
    private final PrintWriter pw;
    private final String host;
    private final int id;
    private Playable player;
    private static final Logger logger = Logger.getLogger(PlayerHandler.class.getSimpleName());

    public PlayerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.host = this.socket.getInetAddress().getHostAddress();
        this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.pw = new PrintWriter(this.socket.getOutputStream(), true);
        this.id = GroupManager.createClientId();
        this.player = new Player();
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
        return player.getChatName();
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
                player.talk(request);
                break;
            case INIT_ALIAS:
                player.setChatName(request.getBody());
                GroupManager.broadcastNewChatter(this);
                break;
            case READY:
                setReady();
                GameManager.tryStartGame();
                break;
            case VOTE:
                player.vote(request);
                break;
            case ACT_ROLE:
                player.targetPlayer(request, player.getRole());
                break;
            default:
                logger.info(String.format("ChatCommand %s \n", command.name()));
        }
    }


    public Role getRole() {
        return player.getRole();
    }

    public boolean isReady() {
        return player.isReady();
    }

    public boolean isAlive() {
        return player.isAlive();
    }

    public void setRole(Role role) {
        player.setRole(role);
    }

    public void setReady() {
        player.setReady();
    }

    public void killInGame() {
        player.killInGame();
    }


}
