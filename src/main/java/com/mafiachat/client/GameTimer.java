package com.mafiachat.client;

import javax.swing.*;

public class GameTimer implements Runnable {
    private int seconds;
    private boolean running;
    private JLabel timerLabel;

    public GameTimer(int seconds, JLabel timerLabel) {
        this.seconds = seconds;
        this.timerLabel = timerLabel;
        this.running = false;
    }

    public void startTimer() {
        running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stopTimer() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            int mins = seconds / 60;
            int secs = seconds % 60;

            String timeString = String.format("%02d:%02d", mins, secs);
            timerLabel.setText(timeString);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            seconds--;
            if (seconds < 0) {
                stopTimer();
            }
        }
    }
}
