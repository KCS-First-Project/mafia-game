package com.mafiachat.server.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MafiaClient {
	public static void main(String[] args) {
		new MafiaClient();
	}
	private String host;
	private int port=1223;
	private Socket socket;
	private JFrame startWindow;
	private JFrame gameWindow;
	MafiaClient() {
		startWindow = new JFrame("MafiaStart");
		startWindow.setSize(500, 400);
		StartPanel startPanel = new StartPanel();
		startWindow.add(startPanel);
		startWindow.setVisible(true);
		gameWindow = new JFrame("MafiaChat");
		
		
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		ChatPanel chatPanel = new ChatPanel();
//
		contentPane.add(chatPanel);
//		
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


	public void connect() 
	{
		try 
		{
			socket = new Socket(host, port);
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
