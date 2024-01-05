package com.mafiachat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
public class ChatMessageReceiver implements Runnable {
	private BufferedReader reader;
	ChatPanel receiver;
	MafiaClient connector;
	public ChatMessageReceiver(MafiaClient c) {
		connector = c;
	}
	public void setMessageReceiver(ChatPanel r) {
		receiver = r;
	}
	
	public void run() {
		String msg;
		try {
			while(connector.socketAvailable()) {
				msg = reader.readLine();
				if(msg == null) {
					System.out.println("Terminating ChatMessageReceiver: message received is null");
					break;
				}
				if(receiver != null) receiver.messageArrived(msg);
			}
		} catch(IOException e) {
			System.out.println("Terminating ChatMessageReceiver: " + e.getMessage());
		} finally  {
			
		}
		
	}
	
	
	
	public void socketClosed() { }
	public void socketConnected(Socket s) throws IOException {
		reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		new Thread(this).start();
	}
}
