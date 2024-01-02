package com.mafiachat.server.handler.Player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;

public interface Playable {
    void talk(ChatRequest request);

    void initAlias(ChatRequest request, ClientHandler newHandler);

    void vote(ChatRequest request);

    void targetPlayer(ChatRequest request, PlayerHandler caller);

    String getChatName();

    Role getRole();

    boolean isReady();

    boolean isAlive();

    void setRole(Role role);

    void setReady();

    void killInGame();
}
