package com.mafiachat.client;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatConnectorImpl;
import com.mafiachat.client.panel.ChatPanel;
import com.mafiachat.client.panel.StartPanel;
import com.mafiachat.client.protocol.ChatMessageReceiver;
import com.mafiachat.util.Constant;
import java.awt.BorderLayout;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class MafiaClient {
    private JFrame startWindow;
    private JFrame gameWindow;
    private final ChatConnector chatConnector;
    private Logger logger = Logger.getLogger(ChatConnector.class.getName());
    private String host;
    private int port;

    MafiaClient() {
        chatConnector = new ChatConnectorImpl();

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        StartPanel startPanel = new StartPanel();
        startWindow = new JFrame("MafiaStart");
        startWindow.setSize(500, 400);
        startWindow.add(startPanel);
        startWindow.setVisible(true);
        startWindow.setResizable(false);
        startWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ChatPanel chatPanel = new ChatPanel(chatConnector);
        contentPane.add(chatPanel);

        ChatMessageReceiver chatReceiver = new ChatMessageReceiver(chatConnector);
        chatReceiver.setMessageReceiver(chatPanel);

        chatConnector.addChatSocketListener(chatPanel);
        chatConnector.addChatSocketListener(chatReceiver);

        gameWindow = new JFrame("MafiaChat");
        gameWindow.setContentPane(contentPane);
        gameWindow.setSize(800, 600);
        gameWindow.setVisible(false);
        gameWindow.setResizable(false);

        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        startPanel.play.addActionListener(e -> {
            setHostAndPortUsingDialog();
            if (!chatConnector.connect(host, port)) {
                return;
            }
            startWindow.setVisible(false);
            gameWindow.setVisible(true);
        });
    }

    public static void main(String[] args) {
        new MafiaClient();
    }

    private void setHostAndPortUsingDialog() {
        host = JOptionPane.showInputDialog("접속 호스트를 입력하세요.");
        try {
            port = Integer.parseInt(JOptionPane.showInputDialog("접속 포트를 입력하세요."));
        } catch (NumberFormatException e) {
            port = Constant.SERVER_PORT;
        }
    }
}
