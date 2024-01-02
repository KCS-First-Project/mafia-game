package main.java.com.mafiachat.server.handler.Player;

import main.java.com.mafiachat.protocol.ChatRequest;
import main.java.com.mafiachat.server.Role;
import main.java.com.mafiachat.server.handler.ClientHandler;
import main.java.com.mafiachat.server.handler.PlayerHandler;

public interface Player {
    void sendNormalMessage(ChatRequest request, Role role);

    void initAlias(String chatName, ChatRequest request, ClientHandler newHandler);

    void vote(ChatRequest request);

    void targetPlayer(ChatRequest request, PlayerHandler caller);
}
