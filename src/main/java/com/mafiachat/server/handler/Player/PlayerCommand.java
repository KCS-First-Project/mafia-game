package com.mafiachat.server.handler.Player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;

public class PlayerCommand implements Player {

    @Override
    public void sendNormalMessage(ChatRequest request, Role role) {
        if ((GameManager.getPhase() == Phase.NIGHT) && (role != Role.CITIZEN)) {
            GameManager.broadcastNormalRoleMessage(role, request);
        } else {
            GroupManager.broadcastMessage(request);
        }
    }

    @Override
    public void initAlias(String chatName, ChatRequest request, ClientHandler newHandler) {
        chatName = request.getBody();
        GroupManager.broadcastNewChatter(newHandler);
    }

    @Override
    public void vote(ChatRequest request) {
        int id = Integer.parseInt(request.getBody());
        GameManager.vote(id);
    }

    @Override
    public void targetPlayer(ChatRequest request, PlayerHandler caller) {
        int id = Integer.parseInt(request.getBody());
        PlayerHandler target = (PlayerHandler) GroupManager.findClientById(id);
        GameManager.setTargetPlayer(caller, target);
    }
}
