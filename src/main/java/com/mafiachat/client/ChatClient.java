//package com.mafiachat.client;
//
//import java.awt.BorderLayout;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import javax.swing.BorderFactory;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import com.mafiachat.client.domain.ConnectionManagerImpl;
//import com.mafiachat.client.event.ConnectionManager;
//
//public class ChatClient extends WindowAdapter {
//    private static final String CHAT_WINDOW_TITLE = "Minimal Chat - Concept Proof";
//    private final ConnectionManager connectionManager;
//    private JFrame chatWindow;
//
//    private ChatClient() {
//        connectionManager = new ConnectionManagerImpl(chatWindow);
//
//        JPanel contentPane = new JPanel(new BorderLayout());
//        contentPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
//        StatusBar status = StatusBar.getStatusBar(connectionManager);
//        status.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
//                BorderFactory.createEmptyBorder(1, 2, 2, 2)));
//        ChatPanel chatPanel = new ChatPanel(connectionManager, status);
//        chatPanel.setBorder(BorderFactory.createEtchedBorder());
//        contentPane.add(status, BorderLayout.SOUTH);
//        ChatMessageReceiver chatReceiver = ChatMessageReceiver.createChatMessageReceiver(connectionManager);
//        chatReceiver.setMessageReceiver(chatPanel);
//
//        chatWindow = new JFrame(CHAT_WINDOW_TITLE);
//        contentPane.add(chatPanel);
//
//        chatWindow.setContentPane(contentPane);
//        chatWindow.setSize(450, 350);
//
//        chatWindow.setLocationRelativeTo(null);
//        chatWindow.setVisible(true);
//        chatWindow.addWindowListener(this);
//
//        ((ConnectionManagerImpl) connectionManager).addChatSocketListener(chatPanel);
//        ((ConnectionManagerImpl) connectionManager).addChatSocketListener(chatReceiver);
//    }
//
//    public void windowClosing(WindowEvent e) {
//        connectionManager.disConnect();
//        System.exit(0);
//    }
//
//    public static ChatClient start() {
//        return new ChatClient();
//    }
//}