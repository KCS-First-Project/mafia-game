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

		play.setOpaque(true); // 추가
		play.setContentAreaFilled(true); // 추가
		play.setBorderPainted(false); // 추가

		quit.setOpaque(true); // 추가
		quit.setContentAreaFilled(true); // 추가
		quit.setBorderPainted(false); // 추가

        play.setBackground(new Color(0, 128, 255)); // 파란색
        play.setForeground(Color.WHITE); // 흰색
		midsLabel=new JLabel("MIDS(Mafia Invasion Dectection System)");
		midsLabel.setBounds(130,200,300,30);
		midsLabel.setForeground(Color.white);
        quit.setBackground(new Color(255, 0, 0)); // 빨간색
        quit.setForeground(Color.WHITE); // 흰색
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
		});

	}
}
