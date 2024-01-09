package com.mafiachat.client;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.event.MessageReceiver;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.Phase;
import com.mafiachat.util.Constant;

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
    JButton vote;
    StringBuilder msgBuilder = new StringBuilder();
    ArrayList<ChatUser> playerList = new ArrayList<ChatUser>(); //생존 플레이어를 확인하기 위한 playerList추가
    Phase phase = Phase.LOBBY;

    private JLabel timerLabel;
    private final GameTimer gameTimer = GameTimer.getInstance();

    public ChatPanel(ChatConnector c) {
        super(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        vote = new JButton("vote");
        vote.setVisible(false);
        chatTextField = new JTextField();
        chatDispArea = new ChatTextPane();//new ChatTextArea();
        userList = new ChatUserList();
        userList.setBackground(new Color(217, 217, 217));
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
        gameTimer.setCountDownListener(timerLabel);

        timerLabel.setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        timerPanel.add(timerLabel);

        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 2;
        c.insets = new Insets(2, 2, 2, 2);
        add(timerLabel, c);

        mafiaLabel.setPreferredSize(new Dimension(50, 50)); // 선호 크기 설정
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(2, 2, 2, 2);
        //왼쪽정렬
        c.anchor = GridBagConstraints.LINE_START;
        add(mafiaLabel, c);


        c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 1;
        c.gridwidth = 2;
        c.insets = new Insets(2, 2, 2, 2);
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
        c.insets = new Insets(0, 0, 1, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(chatTextField, c);


        Ready.addActionListener(this);
        vote.addActionListener(this);
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String msgToSend = chatTextField.getText();
                    ChatRequest request = ChatRequest.createRequest(Command.NORMAL, msgToSend);
                    msgToSend = request.getFormattedMessage();
                    if (msgToSend.trim().equals("")) return;

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
        vote.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        vote.setBackground(Color.blue);
        vote.setForeground(Color.WHITE);
        vote.setPreferredSize(new Dimension(100, 30)); // 필요에 따라 크기 조절
        vote.setFocusPainted(false); // 포커스 테두리 제거
        vote.setBorder(new RoundBorder(20));

        add(vote, c);


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
        switch (command) {
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
                setTimer();
                break;
            case DAY_FIRST_VOTE:
                phase = Phase.DAY_FIRST_VOTE;
                setTimer();
                break;
            case DAY_DEFENSE:
                phase = Phase.DAY_DEFENSE;
                setTimer();
                break;
            case DAY_SECOND_VOTE:
                phase = Phase.DAY_SECOND_VOTE;
                setTimer();
                break;
            case NIGHT:
                phase = Phase.NIGHT;
                setTimer();
                break;
            case UNKNOWN:
                System.out.println("잘못된 명령입니다.");
                break;
            default:
                break;
        }

    }

    private void displayUserList(String users) { //유저리스트를 추가하는 함수 추가(동시로그인시 문제있음)
        String[] strUsers = users.split("\\|");
        String[] nameWithIdHost;
        ArrayList<ChatUser> list = new ArrayList<ChatUser>();
        for (String strUser : strUsers) {
            nameWithIdHost = strUser.split(",");
            list.add(new ChatUser(nameWithIdHost[0], nameWithIdHost[1], nameWithIdHost[2]));
        }
        userList.addNewUsers(list);
    }

    private ArrayList<ChatUser> playUserList(String users) { //플레이 유저 리스트를 갱신하는 함수 추가
        String[] PlayUsers = users.split("\\|");
        String[] nameWithIdHostAlive;
        ArrayList<ChatUser> list = new ArrayList<ChatUser>();
        for (String playUser : PlayUsers) {
            nameWithIdHostAlive = playUser.split(",");
            if (Boolean.parseBoolean(nameWithIdHostAlive[3])) {
                list.add(new ChatUser(nameWithIdHostAlive[0], nameWithIdHostAlive[1], nameWithIdHostAlive[2], Boolean.parseBoolean(nameWithIdHostAlive[3])));
            }
        }

        return list;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //레디버튼누르면 레디보냄
        if (e.getSource() == Ready) {
            String msgToSend;
            ChatRequest readyRequest = ChatRequest.createRequest(Command.READY, "");
            msgToSend = readyRequest.getFormattedMessage();
            System.out.println(msgToSend);
            writer.println(msgToSend);


            String chatName = JOptionPane.showInputDialog(null, "Enter chat name:");
            ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, chatName);
            chatName = request.getFormattedMessage();
            writer.println(chatName);

            System.out.println("sadasd");
            Ready.setVisible(false);
            vote.setVisible(true);
        } else if (e.getSource() == vote) {

            if (phase == Phase.DAY_SECOND_VOTE) {

            }
            //처음 투표시 생존인원 투표
            if (phase == Phase.DAY_FIRST_VOTE) {
                int playerNum = playerList.size();
                String[] votePlayer = new String[8];
                for (int i = 0; i < playerNum; i++) {
                    votePlayer[i] = playerList.get(i).getName();
                }
                //테스트 값들
//				String[] votePlayer = {"태우", "지훈", "혜인", "주현"};
                int killedPlayer = JOptionPane.showOptionDialog(null, "누구에게 투표하시겠습니까?", "플레이어 선택", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, votePlayer, votePlayer[0]);


                System.out.println(killedPlayer);
                //실제 아이디 보내주기
                ChatRequest request = ChatRequest.createRequest(Command.VOTE, playerList.get(killedPlayer).getId());
//				ChatRequest request = ChatRequest.createRequest(Command.VOTE, "1");
                String killP = request.getFormattedMessage();
                writer.println(killP);
                System.out.println(killP);

            }
        }
    }

    private void setTimer() {
        int seconds = phase.timeLimit / Constant.THOUSAND_MILLI_SECOND;
        gameTimer.startTimer(seconds);
    }

    private static class RoundBorder implements Border {
        private int radius;

        public RoundBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(217, 217, 217));
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