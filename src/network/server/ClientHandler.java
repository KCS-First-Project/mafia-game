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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import network.constant.ChatCommandUtil;
import network.exception.NoResourceException;

/**
 * Service
 */
public class ClientHandler implements Runnable, MessageHandler {
    private static final String INVALID_WHISPER = "Invalid whisper format.";
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String chatName, id, host;

    public ClientHandler(Socket s) throws IOException {
        initializeClientHandler(s);
    }

    private void initializeClientHandler(Socket s) throws IOException {
        socket = s;
        host = socket.getInetAddress().getHostAddress();
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(socket.getOutputStream(), true);
        addMessageHandler(this);
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
        removeMessageHandler(this);
        close();
        cleanup();
    }

    @Override
    public void sendMessage(String msg) {
        pw.println(msg);
    }

    @Override
    public String getMessage() throws IOException {
        return br.readLine();
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getId() {
        return id;//socket.getRemoteSocketAddress().toString();
    }

    @Override
    public String getFrom() {
        return host;
    }

    @Override
    public String getName() {
        return chatName;
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
        broadcastMessage(formattedMessage);
    }

    private void handleWhisperMessage(String msg) {
        int delimiterIndex = msg.indexOf('|');
        if (delimiterIndex < 0) {
            throw NoResourceException.errorMessage(INVALID_WHISPER);
        }
        String toId = msg.substring(0, delimiterIndex);
        String msgToWhisper = msg.substring(delimiterIndex + 1);
        String formattedWhisper = formatChatMessage(msgToWhisper);
        sendWhisper(this, toId, formattedWhisper);
    }


    private void handleInitAliasMessage(String msg) {
        String[] nameWithId = msg.split("\\|");
        chatName = nameWithId[0];
        id = nameWithId[1];
        broadcastNewChatter(this);
    }

    private String formatChatMessage(String message) {
        return String.format("%s: %s", chatName, message);
    }
}
