package com.mafiachat.server.handler.Player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;

public interface Playable {
    void talk(ChatRequest request);

    void vote(ChatRequest request);

    void targetPlayer(ChatRequest request, Role role);

    String getChatName();

    Role getRole();

    boolean isReady();

    boolean isAlive();

    void setChatName(String chatName);

    void setRole(Role role);

    void setReady();

    void killInGame();
}
