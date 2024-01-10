package com.mafiachat.client.util;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ImageProvider {
    private ImageIcon mafiaIcon;
    private ImageIcon doctorIcon;
    private ImageIcon gunIcon;
    private ImageIcon policeIcon;
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

    public ImageIcon getDoctorIcon() {
        if (doctorIcon == null) {
            URL imageURL = getClass().getClassLoader().getResource("images/doctor.jpeg");
            assert imageURL != null;
            doctorIcon = new ImageIcon(imageURL);
        }
        return doctorIcon;
    }

    public ImageIcon getScaledDoctorIcon() {
        if (scaledMafiaIcon == null) {
            Image doctorImage = getDoctorIcon().getImage();
            Image scaledMafiaImage = doctorImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            scaledMafiaIcon = new ImageIcon(scaledMafiaImage);
        }
        return scaledMafiaIcon;
    }

    public ImageIcon getGunIcon() {
        if (gunIcon == null) {
            URL imageURL = getClass().getClassLoader().getResource("images/gun.jpeg");
            assert imageURL != null;
            gunIcon = new ImageIcon(imageURL);
        }
        return gunIcon;
    }

    public ImageIcon getScaledGunIcon() {
        if (scaledMafiaIcon == null) {
            Image gunImage = getGunIcon().getImage();
            Image scaledMafiaImage = gunImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            scaledMafiaIcon = new ImageIcon(scaledMafiaImage);
        }
        return scaledMafiaIcon;
    }

    public ImageIcon getPoliceIcon() {
        if (policeIcon == null) {
            URL imageURL = getClass().getClassLoader().getResource("images/police.jpeg");
            assert imageURL != null;
            policeIcon = new ImageIcon(imageURL);
        }
        return policeIcon;
    }

    public ImageIcon getScaledPoliceIcon() {
        if (scaledMafiaIcon == null) {
            Image mafiaImage = getPoliceIcon().getImage();
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
