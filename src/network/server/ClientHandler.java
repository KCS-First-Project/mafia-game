package network.server;

import static network.View.server.ClientHandlerResponseView.cleanup;
import static network.View.server.ClientHandlerResponseView.failToListenMessage;
import static network.View.server.ClientHandlerResponseView.printHandleUnknownCommand;
import static network.View.server.ClientHandlerResponseView.printLineRead;
import static network.constant.ChatCommandUtil.formattingMessage;
import static network.server.GroupManager.addMessageHandler;
import static network.server.GroupManager.broadcastMessage;
import static network.server.GroupManager.broadcastNewChatter;
import static network.server.GroupManager.removeMessageHandler;
import static network.server.GroupManager.sendWhisper;

import java.io.IOException;
import java.net.Socket;
import network.constant.ChatCommandUtil;
import network.exception.NoResourceException;
import network.server.domain.ChatParticipant;
import network.server.domain.ClientCommunicationHandler;


public class ClientHandler implements Runnable, MessageHandler {
    private static final String INVALID_WHISPER = "Invalid whisper format.";
    private ChatParticipant chatParticipant;
    private ClientCommunicationHandler communicationHandler;
    private GroupManager groupManager;

    public ClientHandler(Socket socket) throws IOException {
        communicationHandler = ClientCommunicationHandler.createClientCommunicationHandler(socket);
        groupManager = GroupManager.createGroupManager();
        chatParticipant = ChatParticipant.createChatParticipant(null, null, socket.getInetAddress().getHostAddress());
        addMessageHandler(groupManager.getClientGroup(), this);
    }


    public GroupManager getGroupManager() {
        return groupManager;
    }

    @Override
    public void run() {
        try {
            listenForMessages();
        } catch (IOException e) {
            failToListenMessage(e.getMessage());
        } finally {
            cleanUp();
        }
    }

    private void listenForMessages() throws IOException {
        String msg;
        while ((msg = getMessage()) != null) {
            processMessage(msg);
            printLineRead(msg);
            //GroupManager.broadcastMessage(msg);
        }
    }

    private void cleanUp() {
        removeMessageHandler(groupManager.getClientGroup(), this);
        close();
        cleanup();
    }

    @Override
    public void sendMessage(String msg) {
        communicationHandler.sendMessage(msg);
    }

    @Override
    public String getMessage() throws IOException {
        return communicationHandler.getMessage();
    }

    @Override
    public void close() {
        communicationHandler.close();
    }

    @Override
    public String getId() {
        return chatParticipant.getId();
    }

    @Override
    public String getFrom() {
        return chatParticipant.getFrom();
    }

    @Override
    public String getName() {
        return chatParticipant.getName();
    }

    public void processMessage(String msg) {
        String command = ChatCommandUtil.getCommand(msg);
        msg = formattingMessage(msg);

        if (command.equals(ChatCommandUtil.NORMAL.getCommand())) {
            handleNormalMessage(msg);
            return;
        }
        if (command.equals(ChatCommandUtil.INIT_ALIAS.getCommand())) {
            handleInitAliasMessage(msg);
            return;
        }
        if (command.equals(ChatCommandUtil.WHISPER.getCommand())) {
            handleWhisperMessage(msg);
            return;
        }
        printHandleUnknownCommand(command);
    }

    private void handleNormalMessage(String msg) {
        String formattedMessage = formatChatMessage(msg);
        broadcastMessage(groupManager.getClientGroup(), formattedMessage);
    }

    private void handleWhisperMessage(String msg) {
        int delimiterIndex = msg.indexOf('|');
        if (delimiterIndex < 0) {
            throw NoResourceException.errorMessage(INVALID_WHISPER);
        }
        String toId = msg.substring(0, delimiterIndex);
        String msgToWhisper = msg.substring(delimiterIndex + 1);
        String formattedWhisper = formatChatMessage(msgToWhisper);
        sendWhisper(groupManager.getClientGroup(), this, toId, formattedWhisper);
    }


    private void handleInitAliasMessage(String msg) {
        String[] nameWithId = msg.split("\\|");
        chatParticipant.changeNameAndId(nameWithId);
        broadcastNewChatter(groupManager.getClientGroup(), this);
    }

    private String formatChatMessage(String message) {
        return String.format("%s: %s", chatParticipant.getName(), message);
    }
}
