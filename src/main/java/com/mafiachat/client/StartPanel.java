package com.mafiachat.client;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;

public class StartPanel extends JPanel {

	private JLabel mafiaImage;
	private JLabel midsLabel;
	public JButton play;
	private JButton quit;



	StartPanel(){

		mafiaImage=new JLabel();
		play= new JButton("PLAY");
		quit=new JButton("QUIT");
		Font buttonFont = new Font("Arial", Font.BOLD, 16);
        play.setFont(buttonFont);
        quit.setFont(buttonFont);

		play.setOpaque(true);
		play.setContentAreaFilled(true);
		play.setBorderPainted(false);

		quit.setOpaque(true);
		quit.setContentAreaFilled(true);
		quit.setBorderPainted(false);

        play.setBackground(Color.BLUE);
        play.setForeground(Color.WHITE);
		JLabel midsLabel=new JLabel(MafiaClient.GAME_NAME);
		midsLabel.setBounds(130,200,300,30);
		midsLabel.setForeground(Color.WHITE);
        quit.setBackground(Color.RED);
        quit.setForeground(Color.WHITE);
		mafiaImage.setBounds(150, 50, 200, 170);
		play.setBounds(200, 250, 100, 30);
		quit.setBounds(200, 300, 100, 30);
		mafiaImage.setIcon(new ImageIcon("images/mafia.jpeg"));
		setLayout(null);
        setBackground(Color.BLACK);
        add(midsLabel);
        add(mafiaImage);
        add(play);
        add(quit);

		quit.addActionListener(e -> {
			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
			frame.dispose();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});

	}
}
