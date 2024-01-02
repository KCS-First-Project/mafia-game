//package main.java.com.mafiachat.client;
//
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import main.java.com.mafiachat.View.client.ChatPanelResponseView;
//import main.java.com.mafiachat.client.domain.ChatUser;
//import main.java.com.mafiachat.client.event.ChatSocketListener;
//import main.java.com.mafiachat.client.event.ConnectionManager;
//import main.java.com.mafiachat.client.event.MessageReceiver;
//import main.java.com.mafiachat.protocol.Command;
//
//@SuppressWarnings("serial")
//public class ChatPanel extends JPanel implements MessageReceiver, ActionListener, ChatSocketListener {
//    private static final String VALID_WHISPER = "User to whisper to must be selected";
//    JTextField chatTextField;
//    ChatTextPane chatDisplayArea;
//    ChatUserList userList;
//    CommandButton connectDisconnect;
//    JButton whisper;
//    ChatPanelResponseView writer;
//    ConnectionManager connectionManager;
//    private StatusBar statusBar;
//
//    public ChatPanel(ConnectionManager connectionManager, StatusBar status) {
//        super(new GridBagLayout());
//        this.connectionManager = connectionManager;
//        this.statusBar = status;
//        initUI();
//    }
//
//
//    private void initUI() {
//        chatTextField = new JTextField();
//        chatDisplayArea = new ChatTextPane();//new ChatTextArea();
//        userList = new ChatUserList();
//
//        connectDisconnect = new CommandButton();
//        whisper = new JButton("Whisper");
//
//        chatTextField.setEnabled(false);
//        chatDisplayArea.setEditable(false);
//        whisper.setEnabled(false);
//
//        setupLayout();
//
//        chatTextField.addActionListener(this);
//        connectDisconnect.addActionListener(this);
//        whisper.addActionListener(this);
//    }
//
//    private void setupLayout() {
//        GridBagConstraints c = new GridBagConstraints();
//        JLabel titleLabel = new JLabel("Message Received", JLabel.CENTER);
//        c.gridy = 0;
//        c.gridx = 0;
//        c.insets = new Insets(2, 2, 2, 2);
//        add(titleLabel, c);
//
//        c = new GridBagConstraints();
//        titleLabel = new JLabel("List of Users", JLabel.CENTER);
//        c.gridy = 0;
//        c.gridx = 1;
//        c.gridwidth = 2;
//        c.insets = new Insets(2, 2, 2, 2);
//        add(titleLabel, c);
//
//        c = new GridBagConstraints();
//        c.gridy = 1;
//        c.gridx = 0;
//        c.weighty = 1.0f;
//        c.fill = GridBagConstraints.BOTH;
//        c.weightx = 0.9;
//        c.insets = new Insets(1, 2, 0, 2);
//        JScrollPane scrollPane = new JScrollPane(chatDisplayArea);
//        add(scrollPane, c);
//
//        c = new GridBagConstraints();
//        c.gridy = 1;
//        c.gridx = 1;
//        c.gridwidth = 2;
//        c.weightx = 0.1;
//        c.fill = GridBagConstraints.BOTH;
//        c.anchor = GridBagConstraints.LINE_START;
//        c.insets = new Insets(1, 2, 0, 2);
//        scrollPane = new JScrollPane(userList);
//        add(scrollPane, c);
//
//        c = new GridBagConstraints();
//        c.gridy = 2;
//        c.gridx = 0;
//        c.insets = new Insets(0, 0, 1, 0);
//        c.fill = GridBagConstraints.HORIZONTAL;
//        add(chatTextField, c);
//
//        c = new GridBagConstraints();
//        c.gridy = 2;
//        c.gridx = 1;
//        c.anchor = GridBagConstraints.LINE_START;
//        add(connectDisconnect, c);
//
//        c = new GridBagConstraints();
//        c.gridy = 2;
//        c.gridx = 2;
//        c.anchor = GridBagConstraints.CENTER;
//        add(whisper, c);
//    }
//
//    public void messageArrived(String msg) {
//        String command = Command.parseCommand(msg);
//        System.out.println(msg);
//        msg = Command.parseMessage(msg);
//        if (isMessageCommand(command)) {
//            chatDisplayArea.append(msg + "\n", command);
//        }
//        if (command.equals(Command.USER_LIST.getCommand())) {
//            displayUserList(msg);
//        }
//    }
//
//    private boolean isMessageCommand(String command) {
//        return command.equals(Command.NORMAL.getCommand()) ||
//                command.equals(Command.ENTER_ROOM.getCommand()) ||
//                command.equals(Command.WHISPER.getCommand()) ||
//                command.equals(Command.EXIT_ROOM.getCommand());
//    }
//
//    public void actionPerformed(ActionEvent e) {
//        Object sourceObj = e.getSource();
//        if (sourceObj == chatTextField) {
//            handleChatTextFieldAction();
//        } else if (sourceObj == connectDisconnect) {
//            handleConnectDisconnectAction(e);
//        } else if (sourceObj == whisper) {
//            handleWhisperAction();
//        }
//    }
//
//    private void handleChatTextFieldAction() {
//        String msgToSend = chatTextField.getText().trim();
//        if (!msgToSend.isEmpty() && connectionManager.socketAvailable()) {
//            writer.sendMessage(Command.NORMAL.getCommand(), msgToSend);
//            chatTextField.setText("");
//        }
//    }
//
//    private void handleConnectDisconnectAction(ActionEvent e) {
//        if (CommandButton.CMD_CONNECT.equals(e.getActionCommand())) {
//            if (connectionManager.connect()) {
//                statusBar.changeByChatName();
//                connectDisconnect.toButton(CommandButton.CMD_DISCONNECT);
//            }
//        } else {
//            connectionManager.disConnect();
//            connectDisconnect.toButton(CommandButton.CMD_CONNECT);
//        }
//    }
//
//    private void handleWhisperAction() {
//        ChatUser userToWhisper = (ChatUser) userList.getSelectedValue();
//        if (userToWhisper != null) {
//            String msgToSend = chatTextField.getText().trim();
//            if (!msgToSend.isEmpty()) {
//                writer.sendMessage(Command.WHISPER.getCommand(),
//                        String.format("%s|%s", userToWhisper.getId(), msgToSend));
//                chatTextField.setText("");
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, VALID_WHISPER, Command.WHISPER.getCommand(),
//                    JOptionPane.WARNING_MESSAGE);
//        }
//    }
//
//    public void socketClosed() {
//        chatTextField.setEnabled(false);
//        chatDisplayArea.setEnabled(false);
//        whisper.setEnabled(false);
//        userList.setEnabled(false);
//        connectDisconnect.toButton(CommandButton.CMD_CONNECT);
//    }
//
//    public void socketConnected(Socket socket) throws IOException {
//        writer = ChatPanelResponseView.createChatPanelResponseView(socket);
//        writer.write(writer.createMessage(Command.INIT_ALIAS.getCommand(),
//                String.format("%s|%s", connectionManager.getName(), connectionManager.getId())));
//        chatTextField.setEnabled(true);
//        chatDisplayArea.setEnabled(true);
//        whisper.setEnabled(true);
//        userList.setEnabled(true);
//    }
//
//    private void displayUserList(String users) {
//        List<ChatUser> list = parseUsers(users);
//        updateUserList(list);
//    }
//
//    private List<ChatUser> parseUsers(String users) {
//        List<ChatUser> list = new ArrayList<>();
//        String[] strUsers = users.split("\\|");
//
//        for (String strUser : strUsers) {
//            String[] userDetails = strUser.split(",");
//            if (userDetails.length == 3 && !connectionManager.getId().equals(userDetails[1])) {
//                ChatUser user = new ChatUser(userDetails[0], userDetails[1], userDetails[2]);
//                list.add(user);
//            }
//        }
//        return list;
//    }
//
//    private void updateUserList(List<ChatUser> users) {
//        userList.addNewUsers(users);
//    }
//
//
//}
