package com.mafiachat.client.event;

import com.mafiachat.client.user.ChatUser;
import com.mafiachat.util.Constant;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChatConnectorImpl implements ChatConnector {
    private ChatUser chatUser;
    private Socket socket;
    private Logger logger = Logger.getLogger(ChatConnector.class.getName());
    private List<ChatSocketListener> sListeners = new ArrayList<>();

    @Override
    public boolean connect() {
        if (socketAvailable()) {
            return true;
        }
        try {
            socket = new Socket(Constant.SERVER_HOST, Constant.SERVER_PORT);
            for (ChatSocketListener listener : sListeners) {
                listener.socketConnected(socket);
            }
            return true;
        } catch (IOException e) {
            logger.warning("Couldn't connect: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void disConnect() {
        if (socketAvailable()) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.warning("Failed to close socket: " + e.getMessage());
            }
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean socketAvailable() {
        return !(socket == null || socket.isClosed());
    }

    @Override
    public void invalidateSocket() {
        disConnect();
        for (ChatSocketListener listener : sListeners) {
            listener.socketClosed();
        }
    }

    @Override
    public String getName() {
        return chatUser.getName();
    }

    @Override
    public String getId() {
        return chatUser.getId();
    }

    @Override
    public void addChatSocketListener(ChatSocketListener listener) {
        sListeners.add(listener);
    }

    @Override
    public void removeChatSocketListener(ChatSocketListener listener) {
        sListeners.remove(listener);
    }
}

