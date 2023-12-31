package com.mafiachat.server.manager;

import com.mafiachat.exception.MaxPlayerException;
import com.mafiachat.protocol.*;
import com.mafiachat.server.handler.ClientHandler;

import java.util.Vector;
import java.util.stream.Collectors;

import static com.mafiachat.util.Constant.MAX_PLAYER_NUMBER;

public class GroupManager {
	private static final Vector<ClientHandler> clientGroup = new Vector<>();
	private GroupManager() {}
	public static void addClientHandler(ClientHandler handler) {
		if (clientGroup.size() == MAX_PLAYER_NUMBER) {
			throw new MaxPlayerException("최대 %s인까지 참가 가능합니다.".formatted(MAX_PLAYER_NUMBER));
		}
		clientGroup.add(handler);
		System.out.println("Active clients count: " + clientGroup.size());
	}
	public static void removeClientHandler(ClientHandler handler) {
		clientGroup.remove(handler);
		System.out.println("Active clients count: " + clientGroup.size());
		ChatRequest request = ChatRequest.createRequest(Command.EXIT_ROOM, handler.getClientName() + " has just left chat room");
		broadcastMessage(request);
	}
	public static void broadcastMessage(ChatRequest request, Vector<ClientHandler> receivers) {
		ChatResponse response = ChatResponse.createResponse(request.getCommand(), request.getBody());
		for(ClientHandler handler: receivers) {
			handler.sendMessage(response.getFormattedMessage());
		}
	}

	public static void broadcastMessage(ChatRequest request) {
		broadcastMessage(request, clientGroup);
	}

	public static void notifyUserList() {
		String users = clientGroup.stream().map(
				(client)->"%s,%s".formatted(client.getClientName(), client.getFrom())
		).collect(Collectors.joining("|"));
		ChatRequest request = ChatRequest.createRequest(Command.USER_LIST, users);
		broadcastMessage(request);
	}

	public static void closeAllMessageHandlers() {
		for(ClientHandler handler: clientGroup) {
			handler.close();
		}
		clientGroup.clear();
	}
	public static void broadcastNewChatter(ClientHandler newHandler) {
		Vector<ClientHandler> receivers = clientGroup.stream().filter(
				(client) -> !client.equals(newHandler)
		).collect(Collectors.toCollection(Vector::new));
		ChatRequest request = ChatRequest.createRequest(Command.ENTER_ROOM, (newHandler.getClientName() + " has just entered chat room"));
		broadcastMessage(request, receivers);
		notifyUserList();
	}
}
