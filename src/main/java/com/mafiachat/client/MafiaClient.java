package com.mafiachat.client;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.util.ImageProvider;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;


public class MafiaClient implements ChatConnector {

    public static void main(String[] args) {
        new MafiaClient();
    }

    private String host;
    private int port = 1223;
    private Socket socket;
    private JFrame startWindow;
    private JFrame gameWindow;

    private ArrayList<ChatSocketListener> sListeners = new ArrayList<ChatSocketListener>();

    MafiaClient() {

        startWindow = new JFrame("MafiaStart");
        ImageIcon mafiaIcon = ImageProvider.getInstance().getMafiaIcon();
        startWindow.setIconImage(mafiaIcon.getImage());
        startWindow.setSize(500, 400);
        StartPanel startPanel = new StartPanel();
        startWindow.add(startPanel);
        startWindow.setVisible(true);
        startWindow.setResizable(false);
        startWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameWindow = new JFrame("MafiaChat");
        gameWindow.setIconImage(mafiaIcon.getImage());


        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        ChatPanel chatPanel = new ChatPanel(this);


        ChatMessageReceiver chatReceiver = new ChatMessageReceiver(this);
        chatReceiver.setMessageReceiver(chatPanel);

        contentPane.add(chatPanel);
//		
        gameWindow.setContentPane(contentPane);

        this.addChatSocketListener(chatPanel);
        this.addChatSocketListener(chatReceiver);


        gameWindow.setSize(800, 600);
        gameWindow.setVisible(false);
        gameWindow.setResizable(false);
        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GamePanel gamePanel = new GamePanel();
        startPanel.play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setHostAndPortUsingDialog();
                if (!connect()) {
                    return;
                }
                startWindow.setVisible(false);
                gameWindow.setVisible(true);
            }
        });


    }


    public boolean connect() {
        if (socketAvailable()) return true;
        try {
            socket = new Socket(host, port);
            for (ChatSocketListener lsnr : sListeners) {
                lsnr.socketConnected(socket);

            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void disConnect() {
        if (!(socket.isClosed())) {
            try {
                socket.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public Socket getSocket() {
        return null;
    }


    public boolean socketAvailable() {
        return !(socket == null || socket.isClosed());
    }

    @Override
    public void invalidateSocket() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    public void addChatSocketListener(ChatSocketListener lsnr) {
        sListeners.add(lsnr);
    }

    public void removeChatSocketListener(ChatSocketListener lsnr) {
        sListeners.remove(lsnr);
    }

    private void setHostAndPortUsingDialog() {
        host = JOptionPane.showInputDialog("접속 호스트를 입력하세요.");
        port = Integer.parseInt(JOptionPane.showInputDialog("접속 포트를 입력하세요."));
    }
}
