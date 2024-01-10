package com.mafiachat.client.panel;

import com.mafiachat.client.util.ImageProvider;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class StartPanel extends JPanel {
    private static final String GAME_NAME = "MIDS(Mafia Invasion Detection System)";
    private JLabel mafiaImage;
    public JButton play;
    private JButton quit;

    public StartPanel() {
        setLayout(null);
        setBackground(Color.BLACK);

        mafiaImage = new JLabel();
        mafiaImage.setIcon(ImageProvider.getInstance().getMafiaIcon());
        mafiaImage.setBounds(150, 30, 200, 170);
        add(mafiaImage);

        play = new JButton("PLAY");
        quit = new JButton("QUIT");
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        play.setBounds(200, 250, 100, 30);
        play.setFont(buttonFont);
        play.setOpaque(true);
        play.setContentAreaFilled(true);
        play.setBorderPainted(false);
        play.setBackground(Color.BLUE);
        play.setForeground(Color.WHITE);
        add(play);

        quit.setBounds(200, 300, 100, 30);
        quit.setFont(buttonFont);
        quit.setOpaque(true);
        quit.setContentAreaFilled(true);
        quit.setBorderPainted(false);
        quit.setBackground(Color.RED);
        quit.setForeground(Color.WHITE);
        add(quit);

        JLabel midsLabel = new JLabel(GAME_NAME);
        midsLabel.setBounds(130, 200, 300, 30);
        midsLabel.setForeground(Color.WHITE);
        add(midsLabel);

        quit.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}