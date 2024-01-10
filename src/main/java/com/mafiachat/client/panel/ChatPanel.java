package com.mafiachat.client.panel;

import static com.mafiachat.client.user.ChatUser.createChatUserAlive;
import static com.mafiachat.client.user.ChatUser.createChatUserSetAlive;
import static com.mafiachat.server.Phase.LOBBY;
import static com.mafiachat.util.Constant.THOUSAND_MILLI_SECOND;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.event.MessageReceiver;
import com.mafiachat.client.user.ChatUser;
import com.mafiachat.client.util.ChatTextPane;
import com.mafiachat.client.util.ImageProvider;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class ChatPanel extends JPanel implements MessageReceiver, ActionListener, ChatSocketListener {
    private JTextField chatTextField;
    private ChatTextPane chatDisplayArea;
    private ChatUserList userList;
    private JButton ready;
    private PrintWriter writer;
    private JButton vote;
    private List<ChatUser> playerList = new ArrayList<>();
    private Map<String, String> fistVotedList = new HashMap<>();
    private Phase phase = LOBBY;
    private Role job;
    private final GameTimer gameTimer = GameTimer.getInstance();
    private String playerName = "anonymous";
    private Logger logger = Logger.getLogger(ChatPanel.class.getSimpleName());
    private int killedPlayer;
//    ImageIcon gunIcon = new ImageIcon(
//            new ImageIcon("images/gun.jpeg").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
//    ImageIcon doctorIcon = new ImageIcon(
//            new ImageIcon("images/doctor.jpeg").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
//    ImageIcon policeIcon = new ImageIcon(
//            new ImageIcon("images/police.jpeg").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));

    public ChatPanel(ChatConnector c) {
        super(new GridBagLayout());
        initUI();
    }

    private void initUI() {
        vote = new JButton("vote");
        vote.setVisible(false);

        chatTextField = new JTextField();
        chatTextField.setEnabled(true);

        userList = new ChatUserList();
        userList.setBackground(Color.LIGHT_GRAY);

        chatDisplayArea = new ChatTextPane();
        chatDisplayArea.setEditable(false);

        ready = new JButton("Ready");
        ready.setEnabled(true);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        GridBagConstraints c = new GridBagConstraints();
        //마피아 이미지 크기줄여 붙이기
        ImageIcon scaledMafiaIcon = ImageProvider.getInstance().getScaledMafiaIcon();
        JLabel mafiaLabel = new JLabel(scaledMafiaIcon, JLabel.CENTER);
        JPanel timerPanel = new JPanel();
        timerPanel.setOpaque(false); // 투명하게 설정
        JLabel timerLabel = new JLabel("00:00", JLabel.CENTER);
        gameTimer.setCountDownListener(timerLabel);
        timerLabel.setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        timerPanel.add(timerLabel);

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(timerLabel, gridBagConstraints);

        mafiaLabel.setPreferredSize(new Dimension(50, 50)); // 선호 크기 설정

        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        add(mafiaLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        add(timerPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weighty = 1.0f;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new Insets(1, 2, 0, 2);

        JScrollPane scrollPane = new JScrollPane(chatDisplayArea);
        scrollPane.setViewportBorder(new RoundBorder(15));
        add(scrollPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(1, 2, 0, 2);

        scrollPane = new JScrollPane(userList);
        add(scrollPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(0, 0, 1, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(chatTextField, gridBagConstraints);

        ready.addActionListener(this);
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

            //엔터시 채팅
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String msgToSend = chatTextField.getText();
                    ChatRequest request = ChatRequest.createRequest(Command.NORMAL, playerName + " : " + msgToSend);
                    msgToSend = request.getFormattedMessage();
                    if (msgToSend.trim().equals("")) {
                        return;
                    }
                    writer.println(msgToSend);
                    chatTextField.setText("");
                }
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;

        ready.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        ready.setBackground(new Color(240, 86, 80));
        ready.setForeground(Color.WHITE);
        ready.setPreferredSize(new Dimension(150, 28)); // 필요에 따라 크기 조절
        ready.setBorder(new RoundBorder(10));
        add(ready, gridBagConstraints);

        vote.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        vote.setBackground(new Color(111, 173, 207));
        vote.setForeground(Color.WHITE);
        vote.setPreferredSize(new Dimension(150, 28)); // 필요에 따라 크기 조절
        vote.setFocusPainted(false); // 포커스 테두리 제거
        vote.setBorder(new RoundBorder(10));
        add(vote, gridBagConstraints);
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
            case SYSTEM:
            case NORMAL:
            case ENTER_ROOM:
            case EXIT_ROOM:
                System.out.println(msg);
                chatDisplayArea.append(msg, command);
                break;
            case USER_LIST:
                displayUserList(msg);
                break;
            case PLAYER_LIST:
                playerList = updatePlayUserList(msg);
                break;
            case NOTIFY_ROLE:
                job = Role.valueOf(msg);
                break;
            case VOTED_LIST:
                getFirstVotedList(msg);
                break;
            case LOBBY:
                vote.setVisible(false);
                ready.setVisible(true);
                phase = LOBBY;
                break;
            case DAY_CHAT:
                phase = Phase.DAY_CHAT;
                setTimer();
                break;
            case DAY_FIRST_VOTE:
                setVoteEnabled();
                phase = Phase.DAY_FIRST_VOTE;
                setTimer();
                break;
            case DAY_DEFENSE:
                setVoteDisable();
                phase = Phase.DAY_DEFENSE;
                setTimer();
                break;
            case DAY_SECOND_VOTE:
                setVoteEnabled();
                phase = Phase.DAY_SECOND_VOTE;
                setTimer();
                break;
            case NIGHT:
                setVoteDisable();
                if (job != Role.CITIZEN) {
                    setVoteEnabled();
                }
                phase = Phase.NIGHT;
                setTimer();
                break;
            case ACT_ROLE:
                break;
            case UNKNOWN:
                logger.warning("Valid Command! " + msg);
                break;
            default:
                break;
        }

    }

    //유저리스트 추가하는 함수 추가(동시로그인시 문제있음)
    private void displayUserList(String users) {
        String[] strUsers = users.split("\\|");
        String[] nameWithIdHost;
        List<ChatUser> list = new ArrayList<>();
        for (String strUser : strUsers) {
            nameWithIdHost = strUser.split(",");
            list.add(createChatUserAlive(nameWithIdHost[0], nameWithIdHost[1], nameWithIdHost[2]));
        }
        userList.addNewUsers(list);
    }

    private void getFirstVotedList(String users) {
        List<String> userIdList = Arrays.asList(users.split(","));
        fistVotedList.clear();

        userIdList.forEach(userId ->
                playerList.stream()
                        .filter(player -> userId.equals(player.getId()))
                        .findFirst()
                        .ifPresent(player -> fistVotedList.put(player.getName(), player.getId())));
    }


    //플레이 유저 리스트 갱신
    private List<ChatUser> updatePlayUserList(String users) {
        String[] userEntries = users.split("\\|");
        List<ChatUser> userList = new ArrayList<>();

        for (String userEntry : userEntries) {
            String[] userDetails = userEntry.split(",");

            if (Boolean.parseBoolean(userDetails[3])) {
                String id = userDetails[0];
                String name = userDetails[1];
                String host = userDetails[2];
                boolean isAlive = Boolean.parseBoolean(userDetails[3]);

                userList.add(createChatUserSetAlive(id, name, host, isAlive));
            }
        }

        return userList;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //레디버튼누르면 레디보냄
        if (e.getSource() == ready) {
            String chatName = JOptionPane.showInputDialog(null, "Enter chat name:");
            playerName = chatName;
            ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, chatName);
            chatName = request.getFormattedMessage();
            writer.println(chatName);

            vote.setVisible(true);
            ready.setVisible(false);

            String msgToSend; //ready 위치 수정
            ChatRequest readyRequest = ChatRequest.createRequest(Command.READY, "");
            msgToSend = readyRequest.getFormattedMessage();
            System.out.println(msgToSend);
            writer.println(msgToSend);
            setVoteDisable();
        } else if (e.getSource() == vote) {
            //처음 투표시 생존인원 투표
            if (phase == Phase.DAY_FIRST_VOTE) {
                int playerNum = playerList.size();
                String[] votePlayer = new String[playerNum];
                for (int i = 0; i < playerNum; i++) {
                    votePlayer[i] = playerList.get(i).getName();
                }

                killedPlayer = JOptionPane.showOptionDialog(null, "누구에게 투표하시겠습니까?", "플레이어 선택",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        ImageProvider.getInstance().getScaledGunIcon(), votePlayer, votePlayer[0]);

                System.out.println(killedPlayer);
                //실제 아이디 보내주기
                ChatRequest request = ChatRequest.createRequest(Command.VOTE, playerList.get(killedPlayer).getId());
                String killP = request.getFormattedMessage();
                writer.println(killP);
                System.out.println(killP);
                setVoteDisable();

            } else if (phase == Phase.DAY_SECOND_VOTE) {
                int playerNum = fistVotedList.size();
                String[] votePlayer = new String[playerNum];
                int idx = 0;
                for (String playerId : fistVotedList.keySet()) {
                    votePlayer[idx++] = playerId;
                }
//                votePlayer[playerNum] = "살리기";
                killedPlayer = JOptionPane.showOptionDialog(null, "누구에게 투표하시겠습니까?", "최종 투표",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        ImageProvider.getInstance().getScaledGunIcon(), votePlayer, null);
                ChatRequest request = ChatRequest.createRequest(Command.VOTE,
                        String.valueOf(fistVotedList.get(votePlayer[killedPlayer])));
                String killP = request.getFormattedMessage();
                writer.println(killP);
                System.out.println(killP);
                setVoteDisable();

            } else if (phase == Phase.NIGHT) {
                int playerNum = playerList.size();
                String[] votePlayer = new String[playerNum];
                for (int i = 0; i < playerNum; i++) {
                    votePlayer[i] = playerList.get(i).getName();
                }

                if (job.equals(Role.MAFIA)) {
                    killedPlayer = showJobActionDialog(votePlayer, ImageProvider.getInstance().getScaledGunIcon(),
                            "누구를 죽이시겠습니까?");
                } else if (job.equals(Role.DOCTOR)) {
                    killedPlayer = showJobActionDialog(votePlayer, ImageProvider.getInstance().getDoctorIcon(),
                            "누구를 살리겠습니까?");
                } else if (job.equals(Role.POLICE)) {
                    killedPlayer = showJobActionDialog(votePlayer, ImageProvider.getInstance().getScaledPoliceIcon(),
                            "누구의 직업을 확인하시겠습니까?");
                }

                System.out.println("killedPlayer = " + killedPlayer);
                //실제 아이디 보내주기
                ChatRequest request = ChatRequest.createRequest(Command.ACT_ROLE, playerList.get(killedPlayer).getId());
                String killP = request.getFormattedMessage();
                writer.println(killP);
                System.out.println(killP);
                setVoteDisable();
            }
        }
    }

    private int showJobActionDialog(String[] votePlayer, ImageIcon icon, String message) {
        killedPlayer = JOptionPane.showOptionDialog(
                null,
                message,
                job.description,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                icon,
                votePlayer,
                null
        );
        return killedPlayer;
    }

    private void setTimer() {
        int seconds = phase.timeLimit / THOUSAND_MILLI_SECOND;
        gameTimer.startTimer(seconds);
    }

    private void setVoteDisable() {
        vote.setEnabled(false);
        vote.setBackground(Color.GRAY);
    }

    private void setVoteEnabled() {
        vote.setEnabled(true);
        vote.setBackground(new Color(111, 173, 207));
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