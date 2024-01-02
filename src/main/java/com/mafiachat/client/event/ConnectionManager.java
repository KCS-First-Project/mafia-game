package com.mafiachat.client.event;

import java.net.Socket;

public interface ConnectionManager {
    boolean connect();

    void disConnect();

    Socket getSocket();

    boolean socketAvailable();

    void invalidateSocket();

    String getName();

    String getId();
}

