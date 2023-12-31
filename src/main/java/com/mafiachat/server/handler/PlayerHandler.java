package com.mafiachat.server.handler;

import com.mafiachat.protocol.*;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import com.mafiachat.server.Role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable, ClientHandler {
    private final Socket socket;
    private final BufferedReader br;
    private final PrintWriter pw;
    private final String host;
    private String chatName;
    private boolean ready = false;
    private Role role;

    public PlayerHandler(Socket s) throws IOException {
        socket = s;
        host = socket.getInetAddress().getHostAddress();
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(socket.getOutputStream(), true);
        chatName = "anonymous";
        GroupManager.addClientHandler(this);
        GameManager.addPlayerHandler(this);
    }

    public void run() {
        ChatRequest request;
        try {
            while (true) {
                request = this.getRequest();
                if (request == null) {
                    break;
                }
                processRequest(request);
                System.out.println("lineRead: " + request.getFormattedMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            GroupManager.removeClientHandler(this);
            GameManager.removePlayerHandler(this);
            close();
        }
        System.out.println("Terminating ClientHandler");
    }

    public void sendMessage(String message) {
        pw.println(message);
    }

    public ChatRequest getRequest() throws IOException {
        String formattedMessage = br.readLine();
        return new ChatRequest(formattedMessage);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFrom() {
        return host;
    }

    public String getClientName() {
        return chatName;
    }

    public void processRequest(ChatRequest request) {
        Command command = request.getCommand();
        switch (command) {
            case NORMAL:
                sendNormalMessage(request);
                break;
            case INIT_ALIAS:
                initAlias(request);
                break;
            case READY:
                setReady();
                break;
            default:
                System.out.printf("ChatCommand %s \n", command.name());
        }
    }

    public boolean isReady(){
        return ready;
    }

    public void setRole(Role role){
        this.role = role;
    }

    private void sendNormalMessage(ChatRequest request) {
        GroupManager.broadcastMessage(request);
    }

    private void initAlias(ChatRequest request) {
        chatName = request.getBody();
        GroupManager.broadcastNewChatter(this);
    }

    private void setReady() {
        ready = true;
        GameManager.tryStartGame();
    }
}
