package com.mafiachat.server.handler;

import com.mafiachat.protocol.ChatData;

import java.io.IOException;
public interface ClientHandler {
	public int getId();
	public String getClientName();
	public String getFrom();
	public void sendMessage(String message);
	public ChatData getRequest() throws IOException;
	public void close();
}
