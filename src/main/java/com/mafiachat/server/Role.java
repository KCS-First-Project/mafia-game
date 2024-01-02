package main.java.com.mafiachat.server;

public enum Role {
    CITIZEN("시민"),
    MAFIA("마피아"),
    DOCTOR("의사"),
    POLICE("경찰");

    public final String description;

    Role(String description) {
        this.description = description;
    }
}
