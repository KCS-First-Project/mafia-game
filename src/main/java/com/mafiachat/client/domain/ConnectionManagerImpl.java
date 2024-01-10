package com.mafiachat.client.domain;

import static com.mafiachat.util.Constant.SERVER_PORT;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.event.ConnectionManager;
import com.mafiachat.util.Constant;

public class ConnectionManagerImpl implements ConnectionManager {
    private Socket socket;
    private String chatName;
    private final String id;
    private final List<ChatSocketListener> chatSocketListeners = new ArrayList<>();
    private JFrame chatWindow;

    public ConnectionManagerImpl(JFrame chatWindow) {
        this.id = new java.rmi.server.UID().toString(); // UID can be generated here or passed as a parameter
        this.chatWindow = chatWindow;
    }

    @Override
    public boolean connect() {
        if (socketAvailable()) {
            return true;
        }
        chatName = JOptionPane.showInputDialog(chatWindow, "Enter chat name:");
        if (chatName == null) {
            return false;
        }

        try {
            socket = new Socket(Constant.SERVER_HOST, SERVER_PORT);
            for (ChatSocketListener lsnr : chatSocketListeners) {
                lsnr.socketConnected(socket);
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect chat server", "Eror", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void disConnect() {
        if (socketAvailable()) {
            try {
                socket.close();
            } catch (IOException ignored) {
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
        for (ChatSocketListener listener : chatSocketListeners) {
            listener.socketClosed();
        }
    }

    @Override
    public String getName() {
        return chatName;
    }

    @Override
    public String getId() {
        return id;
    }

    public void addChatSocketListener(ChatSocketListener listener) {
        chatSocketListeners.add(listener);
    }

    public void removeChatSocketListener(ChatSocketListener listener) {
        chatSocketListeners.remove(listener);
    }

}

