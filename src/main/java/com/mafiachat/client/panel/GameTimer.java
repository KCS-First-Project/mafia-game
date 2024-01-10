package com.mafiachat.client.panel;

import javax.swing.JLabel;

public class GameTimer implements Runnable {
    private int totalSeconds;
    private JLabel timerLabel;
    private Thread timerThread;

    private GameTimer() {

    }

    public static GameTimer getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void startTimer(int seconds) {
        this.totalSeconds = seconds;
        stopTimer();
        timerThread = new Thread(this);
        timerThread.start();
    }

    public void stopTimer() {
        if (timerThread == null) {
            return;
        }
        timerThread.interrupt();
    }

    public void setCountDownListener(JLabel timerLabel) {
        this.timerLabel = timerLabel;
    }

    @Override
    public void run() {
        try {
            while (totalSeconds > 0) {
                countDown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            timerThread = null;
        }
    }

    private void countDown() throws InterruptedException {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        if (timerLabel != null) {
            String timeString = String.format("%02d:%02d", minutes, seconds);
            timerLabel.setText(timeString);
        }

        Thread.sleep(1000);

        totalSeconds--;
    }

    private static class LazyHolder {
        private static final GameTimer INSTANCE = new GameTimer();
    }
}
