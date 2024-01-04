package com.mafiachat.server.manager;


import static com.mafiachat.util.Constant.MAX_PLAYER_NUMBER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.mafiachat.exception.MaxPlayerException;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.handler.ClientHandler;

public class GroupManager { ;
    private final String ENTER_ROOM = " has just left chat room";
    private final String EXIT_ROOM = " has just left chat room";
    private int nextClientId = 1;
    private final List<ClientHandler> clientGroup = Collections.synchronizedList(new ArrayList<>());
    private final Logger logger = Logger.getLogger(GameManager.class.getSimpleName());

    private GroupManager() {
    }

    public static GroupManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    synchronized public int createClientId() {
        int id = nextClientId;
        nextClientId++;
        return id;
    }

    public ClientHandler findClientById(int id) {
        Optional<ClientHandler> client = clientGroup.stream()
                .filter((c) -> c.getId() == id)
                .findFirst();
        if (client.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return client.get();
    }

    public String findClientNameById(int id) {
        return findClientById(id).getClientName();
    }

    public void addClientHandler(ClientHandler handler) {
        if (clientGroup.size() == MAX_PLAYER_NUMBER) {
            logger.severe("최대 %s인까지 참가 가능합니다.".formatted(MAX_PLAYER_NUMBER));
            throw new MaxPlayerException();
        }
        clientGroup.add(handler);
        logger.info("Active clients count: " + clientGroup.size());
    }

    public void removeClientHandler(ClientHandler handler) {
        clientGroup.remove(handler);
        logger.info("Active clients count: " + clientGroup.size());
        ChatRequest request = ChatRequest.createRequest(Command.EXIT_ROOM,
                handler.getClientName() + EXIT_ROOM);
        broadcastMessage(request);
    }

    public void multicastMessage(ChatRequest request, List<ClientHandler> receivers) {
        logger.info("broadcast: " + request.getFormattedMessage());
        ChatResponse response = ChatResponse.createResponse(request.getCommand(), request.getBody());
        for (ClientHandler handler : receivers) {
            handler.sendMessage(response.getFormattedMessage());
        }
    }

    public void broadcastMessage(ChatRequest request) {
        multicastMessage(request, clientGroup);
    }

    public void unicastMessage(ChatRequest request, ClientHandler client) {
        logger.info("unicast(%d): %s".formatted(client.getId(), request.getFormattedMessage()));
        ChatResponse response = ChatResponse.createResponse(request.getCommand(), request.getBody());
        client.sendMessage(response.getFormattedMessage());
    }

    public void unicastMessage(ChatRequest request, int id) {
        ClientHandler client = findClientById(id);
        unicastMessage(request, client);
    }

    public void notifyUserList() {
        String users = clientGroup.stream().map(
                (client) -> "%s,%s,%s".formatted(client.getId(), client.getClientName(), client.getFrom())
        ).collect(Collectors.joining("|"));
        ChatRequest request = ChatRequest.createRequest(Command.USER_LIST, users);
        broadcastMessage(request);
    }

    public void closeAllMessageHandlers() {
        for (ClientHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
    }

    public void broadcastNewChatter(ClientHandler newHandler) {
        Vector<ClientHandler> receivers = clientGroup.stream().filter(
                (client) -> !client.equals(newHandler)
        ).collect(Collectors.toCollection(Vector::new));
        ChatRequest request = ChatRequest.createRequest(Command.ENTER_ROOM,
                (newHandler.getClientName() + ENTER_ROOM));
        multicastMessage(request, receivers);
        notifyUserList();
    }

    private static class LazyHolder {
        private static final GroupManager INSTANCE = new GroupManager();
    }
}
