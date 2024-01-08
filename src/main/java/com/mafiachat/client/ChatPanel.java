package com.mafiachat.client;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.event.MessageReceiver;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.Phase;
import com.mafiachat.client.GameTimer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.awt.*;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel implements MessageReceiver, ActionListener, ChatSocketListener {
	JTextField chatTextField;
	ChatTextPane chatDispArea;
	ChatUserList userList;
	JButton Ready;
	PrintWriter writer;
	StringBuilder msgBuilder = new StringBuilder();
	ArrayList<ChatUser> playerList = new ArrayList<ChatUser>(); //생존 플레이어를 확인하기 위한 playerList추가
	Phase phase = Phase.LOBBY;

	private JLabel timerLabel;
	private GameTimer gameTimer;

	public ChatPanel(ChatConnector c) {
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
		timerPanel.setOpaque(false); // 투명하게 설정
		JLabel timerLabel = new JLabel("00:00", JLabel.CENTER);

		timerLabel.setForeground(Color.WHITE);
		setBackground(Color.BLACK);
		timerPanel.add(timerLabel);

		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(2, 2, 2, 2);
		add(timerLabel, c);

		// GameTimer 객체 생성 및 초기화
		gameTimer = new GameTimer(180, timerLabel); // 3분(180초) 타이머로 설정
		// 타이머 시작 -> 현재 play시작하면 바로 시작되도록 구현됨(커스터마이징 필요)
		gameTimer.startTimer();

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
					
					writer.println(msgToSend);
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

	@Override
	public void socketClosed() {

	}

	@Override

	public void socketConnected(Socket s) throws IOException {
		writer = new PrintWriter(s.getOutputStream(), true);
	}

	@Override
	public void messageArrived(ChatResponse response) {
		Command command = response.getCommand();
		System.out.println(response.getCommand());
		String msg = response.getBody();
		switch(command) {
			case NORMAL:
			case SYSTEM:
			case ENTER_ROOM:
			case EXIT_ROOM:
				System.out.println(msg);
				chatDispArea.append(msg);
				break;
			case USER_LIST:
				displayUserList(msg);
				break;
			case PLAYER_LIST:
				playerList = playUserList(msg);
				break;
			case LOBBY:
				phase = Phase.LOBBY;
				break;
			case DAY_CHAT:
				phase = Phase.DAY_CHAT;
				break;
			case DAY_FIRST_VOTE:
				phase = Phase.DAY_FIRST_VOTE;
				break;
			case DAY_DEFENSE:
				phase = Phase.DAY_DEFENSE;
				break;
			case DAY_SECOND_VOTE:
				phase = Phase.DAY_SECOND_VOTE;
				break;
			case NIGHT:
				phase = Phase.NIGHT;
				break;
			case UNKNOWN:
				System.out.println("잘못된 명령입니다.");
				break;
			default:
				break;
		}

	}

	private void displayUserList(String users) { //유저리스트를 추가하는 함수 추가(동시로그인시 문제있음)
		String [] strUsers = users.split("\\|");
		String [] nameWithIdHost;
		ArrayList<ChatUser> list = new ArrayList<ChatUser>();
		for(String strUser : strUsers) {
			nameWithIdHost = strUser.split(",");
			list.add(new ChatUser(nameWithIdHost[0], nameWithIdHost[1], nameWithIdHost[2]));
		}
		userList.addNewUsers(list);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Object sourceObj = e.getSource();
		if(sourceObj == Ready){
			String chatName = JOptionPane.showInputDialog(null, "Enter chat name:");
			ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, chatName);
			chatName=request.getFormattedMessage();

			writer.println(chatName);

			Ready.setVisible(false);
		}
	}






	private ArrayList<ChatUser> playUserList(String users){ //플레이 유저 리스트를 갱신하는 함수 추가
		String [] PlayUsers = users.split("\\|");
		String [] nameWithIdHostAlive;
		ArrayList<ChatUser> list = new ArrayList<ChatUser>();
		for(String playUser : PlayUsers) {
			nameWithIdHostAlive = playUser.split(",");
			list.add(new ChatUser(nameWithIdHostAlive[0], nameWithIdHostAlive[1], nameWithIdHostAlive[2],Boolean.parseBoolean(nameWithIdHostAlive[3])));
		}

		return list;
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