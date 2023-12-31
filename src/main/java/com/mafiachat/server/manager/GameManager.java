package com.mafiachat.server.manager;

import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import com.mafiachat.server.RoleAssignment;
import com.mafiachat.server.handler.PlayerHandler;

import java.util.*;

import static com.mafiachat.util.Constant.MIN_PLAYER_NUMBER;

public class GameManager {
    private static Phase phase = Phase.LOBBY;
    private static final Vector<PlayerHandler> playerGroup = new Vector<>();

    private GameManager(){}

    public static void addPlayerHandler(PlayerHandler handler) {
        playerGroup.add(handler);
    }
    public static void removePlayerHandler(PlayerHandler handler) {
        playerGroup.remove(handler);
    }

    public static void startGame() throws InterruptedException {
        assignRole();
        System.out.println("Game started");
        startDayChat();
    }

    public static void startDayChat() throws InterruptedException {
        //TODO: 전후 로직 추가
        onPhase(Phase.DAY_CHAT);
        startDayFirstVote();
    }

    public static void startDayFirstVote() throws InterruptedException {
        //TODO: 전후 로직 추가
        onPhase(Phase.DAY_FIRST_VOTE);
        startDayDefense();
    }

    public static void startDayDefense() throws InterruptedException {
        //TODO: 전후 로직 추가
        onPhase(Phase.DAY_DEFENSE);
        startDaySecondVote();
    }

    public static void startDaySecondVote() throws InterruptedException {
        //TODO: 전후 로직 추가
        onPhase(Phase.DAY_SECOND_VOTE);
        startNight();
    }

    public static void startNight() throws InterruptedException {
        //TODO: 전후 로직 추가
        onPhase(Phase.NIGHT);
        startDayChat();
    }

    public static void endGame() {
        dismissRole();
        goToLobby();
    }

    public static void goToLobby() {
        phase = Phase.LOBBY;
    }

    public static void tryStartGame() {
        if (playerGroup.size() < MIN_PLAYER_NUMBER) {
            return;
        }
        if (!checkAllReady()) {
            return;
        }
        try {
            startGame();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkAllReady() {
        Optional<Boolean> isAllReady = playerGroup.stream()
                .map(PlayerHandler::isReady)
                .reduce((x, y) -> x && y);
        return isAllReady.isPresent() && isAllReady.get();
    }

    private static void onPhase(Phase phase) throws InterruptedException {
        proceedPhase();
        System.out.printf("%s started%n", phase.name());
        Thread thread = new Thread(()->{
            try {
                Thread.sleep(phase.timeLimit);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        System.out.printf("%s ended%n", phase.name());
    }

    private static void proceedPhase(){
        int nextOrdinal = (phase.ordinal() + 1) % Phase.values().length;
        phase = Phase.values()[nextOrdinal];
    }

    private static void assignRole() {
        Collections.shuffle(playerGroup);
        RoleAssignment roleAssignment = RoleAssignment.getAssignment(playerGroup.size());
        int index = 0;
        int endAt = roleAssignment.citizenNumber;
        for (; index < endAt; index++){
            playerGroup.get(index).setRole(Role.CITIZEN);
        }
        endAt += roleAssignment.mafiaNumber;
        for (; index < endAt; index++){
            playerGroup.get(index).setRole(Role.CITIZEN);
        }
        endAt += roleAssignment.doctorNumber;
        for (; index < endAt; index++){
            playerGroup.get(index).setRole(Role.DOCTOR);
        }
        endAt += roleAssignment.policeNumber;
        for (; index < endAt; index++){
            playerGroup.get(index).setRole(Role.POLICE);
        }
    }

    private static void dismissRole() {
        playerGroup.forEach((player)->player.setRole(null));
    }
}
