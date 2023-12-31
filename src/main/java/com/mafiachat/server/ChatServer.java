package com.mafiachat.server;

import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GroupManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class ChatServer implements Runnable {
	ServerSocket ss;
	public ChatServer() throws IOException {
		ss = new ServerSocket(1223);
		System.out.printf("ChatServer[%s] is listening on port 1223\n", InetAddress.getLocalHost().getHostAddress());
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					cleanup();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void run() {
		Socket s = null;
		try {			
			while(true) {
				s = ss.accept();
				System.out.format("Client[%s] accepted\n", s.getInetAddress().getHostName());
				new Thread(new PlayerHandler(s)).start();
			}
		} catch(IOException e) {
			System.out.println("Terminating ChatServer: " + e.getMessage());
		}
		System.out.println("ChatServer shut down");
	}
	public void cleanup() throws IOException {
		ss.close();
		GroupManager.closeAllMessageHandlers();
	}
	public static void main(String [] args) {
		try {
			Runnable r = new ChatServer();
			new Thread(r).start();
		} catch(IOException e) {
			System.out.println("Failed to start server: " + e.getMessage());
		}
	}
}
