package com.mafiachat.client.event;

import java.net.Socket;

public interface ChatConnector {
    boolean connect();

    void disConnect();

    Socket getSocket();

    boolean socketAvailable();

    void invalidateSocket();

    String getName();

    String getId();

    void addChatSocketListener(ChatSocketListener listener);

    void removeChatSocketListener(ChatSocketListener listener);
}