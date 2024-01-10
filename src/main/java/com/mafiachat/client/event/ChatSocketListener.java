package com.mafiachat.client.event;

import java.io.IOException;

public interface ChatSocketListener {
    void socketClosed();

    void socketConnected(java.net.Socket s) throws IOException;
}
