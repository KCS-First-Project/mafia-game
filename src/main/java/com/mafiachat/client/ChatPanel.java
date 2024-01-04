package com.mafiachat.client;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel {
	JTextField chatTextField;
	ChatTextPane chatDispArea;
	ChatUserList userList;
	JButton Ready;
	PrintWriter writer;
	StringBuilder msgBuilder = new StringBuilder();
	public ChatPanel() {
		super(new GridBagLayout());
		initUI();
	}
	
	private void initUI() {
		chatTextField = new JTextField();
		chatDispArea = new ChatTextPane();//new ChatTextArea();
		userList = new ChatUserList();
		userList.setBackground(new Color(217,217,217));
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
		
		scrollPane.setViewportBorder(new RoundBorder(15));
		
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