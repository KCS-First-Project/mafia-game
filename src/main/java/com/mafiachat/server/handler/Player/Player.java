package com.mafiachat.server.handler.Player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;

public interface Player {

    void sendNormalMessage(ChatRequest request, Role role);

    void initAlias(String chatName, ChatRequest request, ClientHandler newHandler);

    void vote(ChatRequest request);

    void targetPlayer(ChatRequest request, PlayerHandler caller);
}
