package com.mafiachat.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MafiaClient {
	public static void main(String[] args) {
		new MafiaClient();
	}
	private String host="192.168.203.81";
	private int port=1223;
	Socket socket;
	private JFrame startWindow;
	private JFrame gameWindow;
	MafiaClient() {
		connect();
		startWindow = new JFrame("MafiaStart");
		startWindow.setSize(500, 400);
		StartPanel startPanel = new StartPanel();
		startWindow.add(startPanel);
		startWindow.setVisible(true);
		gameWindow = new JFrame("MafiaChat");
		
		
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		ChatPanel chatPanel = new ChatPanel(this);
//
		contentPane.add(chatPanel);
		ChatMessageReceiver chatReceiver = new ChatMessageReceiver(this);
		chatReceiver.setMessageReceiver(chatPanel);
		gameWindow.setContentPane(contentPane);
		gameWindow.setSize(500, 400);
		gameWindow.setVisible(false);
		GamePanel gamePanel = new GamePanel();
		startPanel.play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startWindow.setVisible(false);	
				gameWindow.setVisible(true);
				
			}	
		});
	
		
	}


	public boolean connect() 
	{
		try 
		{
			socket = new Socket(host, port);
			return true;
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public void disConnect() 
	{
		if(!(socket.isClosed())) 
		{
			try 
			{
				socket.close();
			} catch(IOException ex) 
			{
			}
		}
	}
	
	
	
	public boolean socketAvailable() 
	{
		return !(socket == null || socket.isClosed());
	}
}
