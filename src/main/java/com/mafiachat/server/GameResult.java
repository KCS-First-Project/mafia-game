package main.java.com.mafiachat.server;

public enum GameResult {
    RESUME(""),
    MAFIA_WIN("시민 측 승리"),
    CITIZEN_WIN("마피아 측 승리");

    public final String announceMessage;

    GameResult(String announceMessage) {
        this.announceMessage = announceMessage;
    }
}
