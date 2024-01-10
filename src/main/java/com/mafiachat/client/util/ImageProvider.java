package com.mafiachat.client.util;

import com.mafiachat.client.GameTimer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageProvider {
    private ImageIcon mafiaIcon;
    private ImageIcon scaledMafiaIcon;

    public ImageIcon getMafiaIcon() {
        if (mafiaIcon == null) {
            URL imageURL = getClass().getClassLoader().getResource("images/mafia.jpeg");
            assert imageURL != null;
            mafiaIcon = new ImageIcon(imageURL);
        }
        return mafiaIcon;
    }

    public ImageIcon getScaledMafiaIcon() {
        if (scaledMafiaIcon == null) {
            Image mafiaImage = getMafiaIcon().getImage();
            Image scaledMafiaImage = mafiaImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            scaledMafiaIcon = new ImageIcon(scaledMafiaImage);
        }
        return scaledMafiaIcon;
    }

    public static ImageProvider getInstance() {
        return ImageProvider.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ImageProvider INSTANCE = new ImageProvider();
    }
}
