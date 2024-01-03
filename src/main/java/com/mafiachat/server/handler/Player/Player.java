package com.mafiachat.server.handler.Player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;

import static com.mafiachat.util.Constant.BASIC_CHAT_NAME;

public class Player implements Playable {

    private final GameManager gameManager;
    private String chatName = BASIC_CHAT_NAME;
    private Role role;
    private boolean ready = false;
    private boolean alive = true;

    public Player(GameManager gameManager){
        this.gameManager = gameManager;
    }

    @Override
    public void talk(ChatRequest request) {
        if ((gameManager.getPhase() == Phase.NIGHT) && (role != Role.CITIZEN)) {
            gameManager.broadcastNormalRoleMessage(role, request);
        } else {
            gameManager.broadcastMessage(request);
        }
    }

    @Override
    public void vote(ChatRequest request) {
        int id = Integer.parseInt(request.getBody());
        gameManager.vote(id);
    }

    @Override
    public void targetPlayer(ChatRequest request, Role role) {
        int id = Integer.parseInt(request.getBody());
        PlayerHandler target = gameManager.findPlayerById(id);
        gameManager.setTargetPlayer(role, target);
    }

    @Override
    public String getChatName() {
        return this.chatName;
    }

    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    @Override
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public void setReady() {
        this.ready = true;
    }

    @Override
    public void killInGame() {
        this.alive = false;
    }
}
