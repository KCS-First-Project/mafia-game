package com.mafiachat.server;

import java.util.HashMap;

import static com.mafiachat.util.Constant.MAX_PLAYER_NUMBER;
import static com.mafiachat.util.Constant.MIN_PLAYER_NUMBER;

public enum RoleAssignment {
    FIVE_PLAYERS(3, 1, 1, 0),
    SIX_PLAYERS(3, 1, 1, 1),
    SEVEN_PLAYERS(3, 2, 1, 1),
    EIGHT_PLAYERS(4, 2, 1, 1);

    public final int citizenNumber;
    public final int mafiaNumber;
    public final int doctorNumber;
    public final int policeNumber;

    private static final HashMap<Integer, RoleAssignment> playerNumber2RoleAssignment = initPlayerNumber2RoleAssignment();

    RoleAssignment(int citizenNumber, int mafiaNumber, int doctorNumber, int policeNumber) {
        this.citizenNumber = citizenNumber;
        this.mafiaNumber = mafiaNumber;
        this.doctorNumber = doctorNumber;
        this.policeNumber = policeNumber;
    }

    public static RoleAssignment getAssignment(int playerNumber) {
        return playerNumber2RoleAssignment.get(playerNumber);
    }

    private static HashMap<Integer, RoleAssignment> initPlayerNumber2RoleAssignment() {
        HashMap<Integer, RoleAssignment> playerNumber2RoleAssignment = new HashMap<>();
        int playerNumber = MIN_PLAYER_NUMBER;
        for (RoleAssignment roleAssignment : RoleAssignment.values()) {
            playerNumber2RoleAssignment.put(playerNumber, roleAssignment);
            playerNumber++;
        }
        return playerNumber2RoleAssignment;
    }
}
