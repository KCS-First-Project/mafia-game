package com.mafiachat.server.manager;

import com.mafiachat.exception.MaxPlayerException;
import com.mafiachat.protocol.*;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mafiachat.util.Constant.MAX_PLAYER_NUMBER;

public class GroupManager {
    private static int nextClientId = 1;
    private static final List<ClientHandler> clientGroup = Collections.synchronizedList(new ArrayList<>());
    private static final Logger logger = Logger.getLogger(GameManager.class.getSimpleName());

    private GroupManager() {
    }

    synchronized public static int createClientId() {
        int id = nextClientId;
        nextClientId++;
        return id;
    }

    public static ClientHandler findClientById(int id) {
        Optional<ClientHandler> client = clientGroup.stream()
                .filter((c) -> c.getId() == id)
                .findFirst();
        if (client.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return client.get();
    }

    public static String findClientNameById(int id) {
        return findClientById(id).getClientName();
    }

    public static void addClientHandler(ClientHandler handler) {
        if (clientGroup.size() == MAX_PLAYER_NUMBER) {
            throw new MaxPlayerException("최대 %s인까지 참가 가능합니다.".formatted(MAX_PLAYER_NUMBER));
        }
        clientGroup.add(handler);
        logger.log(Level.INFO, "Active clients count: " + clientGroup.size());
    }

    public static void removeClientHandler(ClientHandler handler) {
        clientGroup.remove(handler);
        logger.log(Level.INFO, "Active clients count: " + clientGroup.size());
        ChatRequest request = ChatRequest.createRequest(Command.EXIT_ROOM, handler.getClientName() + " has just left chat room");
        broadcastMessage(request);
    }

    public static void multicastMessage(ChatRequest request, List<ClientHandler> receivers) {
        logger.log(Level.INFO, "broadcast: " + request.getFormattedMessage());
        ChatResponse response = ChatResponse.createResponse(request.getCommand(), request.getBody());
        for (ClientHandler handler : receivers) {
            handler.sendMessage(response.getFormattedMessage());
        }
    }

    public static void broadcastMessage(ChatRequest request) {
        multicastMessage(request, clientGroup);
    }

    public static void unicastMessage(ChatRequest request, ClientHandler client) {
        logger.log(Level.INFO, "unicast(%d): %s".formatted(client.getId(), request.getFormattedMessage()));
        ChatResponse response = ChatResponse.createResponse(request.getCommand(), request.getBody());
        client.sendMessage(response.getFormattedMessage());
    }

    public static void unicastMessage(ChatRequest request, int id) {
        ClientHandler client = findClientById(id);
        unicastMessage(request, client);
    }

    public static void notifyUserList() {
        String users = clientGroup.stream().map(
                (client) -> "%s,%s,%s".formatted(client.getId(), client.getClientName(), client.getFrom())
        ).collect(Collectors.joining("|"));
        ChatRequest request = ChatRequest.createRequest(Command.USER_LIST, users);
        broadcastMessage(request);
    }

    public static void closeAllMessageHandlers() {
        for (ClientHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
    }

    public static void broadcastNewChatter(ClientHandler newHandler) {
        Vector<ClientHandler> receivers = clientGroup.stream().filter(
                (client) -> !client.equals(newHandler)
        ).collect(Collectors.toCollection(Vector::new));
        ChatRequest request = ChatRequest.createRequest(Command.ENTER_ROOM, (newHandler.getClientName() + " has just entered chat room"));
        multicastMessage(request, receivers);
        notifyUserList();
    }
}
