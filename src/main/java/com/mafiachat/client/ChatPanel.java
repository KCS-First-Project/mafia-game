package com.mafiachat.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel implements ActionListener {
	JTextField chatTextField;
	ChatTextPane chatDispArea;
//	TextArea chatDispArea;
	ChatUserList userList;
	JButton Ready;
	PrintWriter writer;
	StringBuilder msgBuilder = new StringBuilder();
	private MafiaClient connector;
	public ChatPanel(MafiaClient c) {
		super(new GridBagLayout());
		connector = c;
		initUI();
		try {
			socketConnected(connector.socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initUI() {
		chatTextField = new JTextField();
//		chatDispArea = new ChatTextPane();//new ChatTextArea();
		chatDispArea = new ChatTextPane();
		userList = new ChatUserList();
		userList.setBackground(new Color(217,217,217));
		chatDispArea.setBackground(new Color(217,217,217));
		Ready = new JButton("Ready");
		
		chatTextField.setEnabled(true);
		chatDispArea.setEditable(false);
		Ready.setEnabled(true);
		
		
		GridBagConstraints c = new GridBagConstraints();
		//마피아 이미지 크기줄여 붙이기
		ImageIcon mafiaIcon = new ImageIcon("images/mafia.jpeg");
		Image mafiaImage = mafiaIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		ImageIcon scaledMafiaIcon = new ImageIcon(mafiaImage);
		JLabel mafiaLabel = new JLabel(scaledMafiaIcon, JLabel.CENTER);
		JPanel timerPanel = new JPanel();
		JLabel timerLabel = new JLabel("00:00", JLabel.CENTER);
		//panel등 색깔설정
		setBackground(Color.black);
		timerPanel.add(timerLabel);
		
		mafiaLabel.setPreferredSize(new Dimension(50, 50)); // 선호 크기 설정
		c.gridy = 0;
		c.gridx = 0;
		c.insets = new Insets(2,2,2,2);
		//왼쪽정렬
		c.anchor = GridBagConstraints.LINE_START;
		add(mafiaLabel, c);
		
		
	     
	        
	        
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(2,2,2,2);
		add(timerPanel, c);
		
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 0;
		c.weighty = 1.0f;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.9;
		c.insets = new Insets(1, 2, 0, 2);
		JScrollPane scrollPane = new JScrollPane(chatDispArea);
		
//		scrollPane.setViewportBorder(new RoundBorder(15));
		
		add(scrollPane, c);
		
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = 2;
		c.weightx = 0.1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(1, 2, 0, 2);
		scrollPane = new JScrollPane(userList);
//		setViewportBorder(scrollPane,new RoundBorder(15)); // 20은 둥근 정도, 필요에 따라 조절
		
		add(scrollPane, c);
		
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 0;
		c.insets = new Insets(0,0, 1, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(chatTextField, c);
		
		  
		Ready.addActionListener(this);
		chatTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			//엔터시채팅
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String msgToSend = chatTextField.getText();
					ChatRequest request = ChatRequest.createRequest(Command.NORMAL, msgToSend);
					msgToSend=request.getFormattedMessage();
					if(msgToSend.trim().equals("")) return;
					if(connector.socketAvailable()) {
						sendMessage(msgToSend);
					}
					chatTextField.setText("");
				}
				
			}
			
		});

	


		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
//		add(connectDisconnect, c);
		
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 2;
		c.anchor = GridBagConstraints.CENTER;
		Ready.setFont(new Font("맑은 고딕", Font.BOLD, 14));
	    Ready.setBackground(Color.BLUE);
	    Ready.setForeground(Color.WHITE);
//	    Ready.setFocusPainted(false); // 포커스 테두리 제거
	    Ready.setPreferredSize(new Dimension(100, 30)); // 필요에 따라 크기 조절
		Ready.setBorder(new RoundBorder(10));
		
		
		add(Ready, c);
	}
	
	
	
	//메세지도착
	public void messageArrived(String msg) {
//		msg = msg.replaceFirst("\\[{1}[a-z]\\]{1}", "");
		chatDispArea.append(msg + "\n");
		}
	//ready누르면 비활성화후 작업
	public void actionPerformed(ActionEvent e) {
		Object sourceObj = e.getSource();
		if(sourceObj == Ready) {
			Ready.setEnabled(false);
		}
		
		
	}
	
	private void sendMessage(String msgToSend) {
		writer.println(msgToSend);
	}

	public void socketConnected(Socket s) throws IOException {
		writer = new PrintWriter(s.getOutputStream(), true);
	}
	private static class RoundBorder implements Border {
        private int radius;

        public RoundBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        	g.setColor(new Color(217,217,217));
            g.drawRoundRect(x, y, width, height, radius, radius);
        }

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(radius, radius, radius, radius);
		}

		@Override
		public boolean isBorderOpaque() {
			// TODO Auto-generated method stub
			return true;
		}
    }
    
}