package com.mafiachat.server.handler;

import java.io.IOException;
import com.mafiachat.protocol.ChatData;

public interface ClientHandler {
    public int getId();

    public String getClientName();

    public String getFrom();

    public void sendMessage(String message);

    public ChatData getRequest() throws IOException;

    public void close();
}
