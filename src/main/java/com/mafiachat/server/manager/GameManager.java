package com.mafiachat.server.manager;


import static com.mafiachat.util.Constant.MIN_PLAYER_NUMBER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.GameResult;
import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import com.mafiachat.server.RoleAssignment;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;

public class GameManager {
    private static Thread gameThread = null;
    private static Phase phase = Phase.LOBBY;
    private static final List<PlayerHandler> playerGroup = Collections.synchronizedList(new ArrayList<>());
    private static final HashMap<Integer, Integer> id2VoteCount = new HashMap<>();
    private static final HashMap<Role, PlayerHandler> role2TargetPlayer = new HashMap<>();
    private static final Logger logger = Logger.getLogger(GameManager.class.getSimpleName());

    private GameManager() {
    }

    public static void addPlayerHandler(PlayerHandler handler) {
        playerGroup.add(handler);
    }

    public static void removePlayerHandler(PlayerHandler handler) {
        playerGroup.remove(handler);
    }

    synchronized public static boolean tryStartGame() {
        if (playerGroup.size() < MIN_PLAYER_NUMBER) {
            logger.info("5인 이상부터 플레이 가능합니다.");
            return false;
        }
        if (!checkAllReady()) {
            logger.info("아직 준비되지 않은 플레이어가 있습니다.");
            return false;
        }
        if (gameThread != null) {
            return true;
        }
        gameThread = new Thread(() -> {
            try {
                startGame();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        gameThread.start();
        return true;
    }

    public static Phase getPhase() {
        return phase;
    }

    public static void delay(int milliSeconds) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(milliSeconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
    }

    public static void setAllPlayersReady() {
        //디버그용
        playerGroup.forEach(PlayerHandler::setReady);
    }

    synchronized public static void vote(int id) {
        int voteCount = id2VoteCount.getOrDefault(id, 0);
        id2VoteCount.put(id, voteCount);
    }

    public static int getVoteCountById(int id) {
        return id2VoteCount.getOrDefault(id, 0);
    }

    public static List<Integer> getMostVotedPlayerIds() {
        OptionalInt maxVoteCount = playerGroup.stream()
                .mapToInt((c) -> getVoteCountById(c.getId()))
                .max();
        if (maxVoteCount.isEmpty() || (maxVoteCount.getAsInt() == 0)) {
            return new ArrayList<>();
        }
        return playerGroup.stream()
                .map(PlayerHandler::getId)
                .filter((id) -> getVoteCountById(id) == maxVoteCount.getAsInt())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    synchronized public static void setTargetPlayer(PlayerHandler caller, PlayerHandler target) {
        if (caller.getRole() == Role.CITIZEN) {
            return;
        }
        if (!target.isAlive()) {
            return;
        }
        PlayerHandler old = role2TargetPlayer.getOrDefault(caller.getRole(), null);
        if (old != null) {
            return;
        }
        role2TargetPlayer.put(caller.getRole(), target);
    }

    public static void broadcastNormalRoleMessage(Role role, ChatRequest request) {
        if (role == Role.CITIZEN) {
            return;
        }
        List<ClientHandler> players = findPlayersByRole(role);
        GroupManager.multicastMessage(request, players);
    }

    public static Thread getGameThread() {
        return gameThread;
    }

    private static void startGame() throws InterruptedException {
        assignRole();
        announceRole();
        phase = Phase.LOBBY;
        id2VoteCount.clear();
        role2TargetPlayer.clear();

        logger.log(Level.INFO, "Game started");
        startDayChat();
    }

    private static void startDayChat() throws InterruptedException {
        GameResult gameResult = getGameResult();
        if (gameResult != GameResult.RESUME) {
            endGame(gameResult);
        }

        onPhase(Phase.DAY_CHAT);

        startDayFirstVote();
    }

    private static void startDayFirstVote() throws InterruptedException {
        id2VoteCount.clear();

        onPhase(Phase.DAY_FIRST_VOTE);

        List<Integer> mostVotedPlayerIds = getMostVotedPlayerIds();
        if (mostVotedPlayerIds.isEmpty()) {
            announceKilledPlayer("투표 결과", null);
            startNight();
        } else {
            startDayDefense(mostVotedPlayerIds);
        }
    }

    private static void startDayDefense(List<Integer> mostVotedPlayerIds) throws InterruptedException {
        String announceMessage = mostVotedPlayerIds.stream()
                .map((id) -> "%s(%d)".formatted(GroupManager.findClientNameById(id), id))
                .collect(Collectors.joining(", "));
        ChatRequest request = ChatRequest.createSystemRequest(announceMessage);
        GroupManager.broadcastMessage(request);

        onPhase(Phase.DAY_DEFENSE);

        startDaySecondVote(mostVotedPlayerIds);
    }

    private static void startDaySecondVote(List<Integer> mostVotedPlayerIds) throws InterruptedException {
        id2VoteCount.clear();

        onPhase(Phase.DAY_SECOND_VOTE);

        List<Integer> oldMostVotedPlayerIds = mostVotedPlayerIds;
        mostVotedPlayerIds = getMostVotedPlayerIds().stream()
                .filter(oldMostVotedPlayerIds::contains)
                .collect(Collectors.toList());
        PlayerHandler playerToKill = getPlayerToKill(mostVotedPlayerIds);
        killVotedPlayer(playerToKill);
        announceKilledPlayer("투표 결과", playerToKill);
        GameResult gameResult = getGameResult();
        if (gameResult == GameResult.RESUME) {
            startNight();
        } else {
            endGame(gameResult);
        }
    }

    private static void startNight() throws InterruptedException {
        onPhase(Phase.NIGHT);

        PlayerHandler killedByMafia = applyRoleAction();
        announceKilledPlayer("밤 사이에", killedByMafia);
        startDayChat();
    }

    private static void endGame(GameResult gameResult) {
        announceResult(gameResult);
        dismissRole();
        goToLobby();
    }

    private static void goToLobby() {
        phase = Phase.LOBBY;
    }

    private static boolean checkAllReady() {
        Optional<Boolean> isAllReady = playerGroup.stream()
                .map(PlayerHandler::isReady)
                .reduce((x, y) -> x && y);
        return isAllReady.isPresent() && isAllReady.get();
    }

    private static void onPhase(Phase phase) throws InterruptedException {
        proceedPhase();

        ChatRequest phaseNotiRequest = ChatRequest.createRequest(Command.valueOf(phase.name()), "");
        GroupManager.broadcastMessage(phaseNotiRequest);
        ChatRequest phaseAnnounceRequest = ChatRequest.createSystemRequest(phase.announceMessage);
        GroupManager.broadcastMessage(phaseAnnounceRequest);

        logger.log(Level.INFO, "%s started".formatted(phase.name()));
        delay(phase.timeLimit);
        logger.log(Level.INFO, "%s ended".formatted(phase.name()));
    }

    private static void proceedPhase() {
        int nextOrdinal = (phase.ordinal() + 1) % Phase.values().length;
        phase = Phase.values()[nextOrdinal];
    }

    private static void assignRole() {
        Collections.shuffle(playerGroup);
        RoleAssignment roleAssignment = RoleAssignment.getAssignment(playerGroup.size());
        int index = 0;
        int endAt = roleAssignment.citizenNumber;
        for (; index < endAt; index++) {
            playerGroup.get(index).setRole(Role.CITIZEN);
        }
        endAt += roleAssignment.mafiaNumber;
        for (; index < endAt; index++) {
            playerGroup.get(index).setRole(Role.MAFIA);
        }
        endAt += roleAssignment.doctorNumber;
        for (; index < endAt; index++) {
            playerGroup.get(index).setRole(Role.DOCTOR);
        }
        endAt += roleAssignment.policeNumber;
        for (; index < endAt; index++) {
            playerGroup.get(index).setRole(Role.POLICE);
        }
    }

    private static void dismissRole() {
        playerGroup.forEach((player) -> player.setRole(null));
    }

    private static PlayerHandler getPlayerToKill(List<Integer> mostVotedPlayerIds) {
        if (mostVotedPlayerIds.isEmpty()) {
            return null;
        }
        Collections.shuffle(mostVotedPlayerIds);
        int votedPlayerId = mostVotedPlayerIds.get(0);
        return (PlayerHandler) GroupManager.findClientById(votedPlayerId);
    }

    private static void killVotedPlayer(PlayerHandler player) {
        if (player == null) {
            return;
        }
        player.killInGame();
        notifyPlayerList();
    }

    private static void notifyPlayerList() {
        String players = playerGroup.stream().map((player) ->
                "%s,%s,%s,%s".formatted(
                        player.getId(),
                        player.getClientName(),
                        player.getFrom(),
                        player.isAlive()
                )
        ).collect(Collectors.joining("|"));
        ChatRequest request = ChatRequest.createRequest(Command.PLAYER_LIST, players);
        GroupManager.broadcastMessage(request);
    }

    private static PlayerHandler applyRoleAction() {
        PlayerHandler mafiaTargetPlayer = role2TargetPlayer.getOrDefault(Role.MAFIA, null);
        PlayerHandler doctorTargetPlayer = role2TargetPlayer.getOrDefault(Role.DOCTOR, null);
        PlayerHandler policeTargetPlayer = role2TargetPlayer.getOrDefault(Role.POLICE, null);

        if (policeTargetPlayer != null) {
            ChatRequest request = ChatRequest.createSystemRequest(
                    "%s(%d) = %s".formatted(policeTargetPlayer.getClientName(), policeTargetPlayer.getId(),
                            policeTargetPlayer.getRole().name())
            );
            broadcastSystemRoleMessage(Role.POLICE, request);
        }

        if ((mafiaTargetPlayer != null) && (mafiaTargetPlayer != doctorTargetPlayer)) {
            mafiaTargetPlayer.killInGame();
            return mafiaTargetPlayer;
        }
        return null;
    }

    private static void broadcastSystemRoleMessage(Role role, ChatRequest request) {
        if (role == Role.CITIZEN) {
            return;
        }
        List<ClientHandler> players = findPlayersByRole(role);
        GroupManager.multicastMessage(request, players);
    }

    private static List<ClientHandler> findPlayersByRole(Role role) {
        return playerGroup.stream()
                .filter((p) -> p.getRole() == role)
                .collect(Collectors.toList());
    }


    private static void announceRole() {
        for (PlayerHandler player : playerGroup) {
            ChatRequest request = ChatRequest.createSystemRequest(
                    "당신의 역할은 %s입니다.".formatted(player.getRole().description));
            GroupManager.unicastMessage(request, player);
        }
    }

    private static void announceKilledPlayer(String context, PlayerHandler killedPlayer) {
        String announceMessage;
        if (killedPlayer == null) {
            announceMessage = "%s 아무도 죽지 않았습니다.".formatted(context);
        } else {
            announceMessage = "%s %s(%d) 플레이어가 죽었습니다.".formatted(context, killedPlayer.getClientName(),
                    killedPlayer.getId());
        }
        ChatRequest request = ChatRequest.createSystemRequest(announceMessage);
        GroupManager.broadcastMessage(request);
    }

    private static void announceResult(GameResult gameResult) {
        String userRoleList = playerGroup.stream()
                .map((p) -> "%s(%d) 플레이어는 %s입니다.".formatted(p.getClientName(), p.getId(), p.getRole().description))
                .collect(Collectors.joining("\n"));
        String announceMessage = "%s\n%s".formatted(gameResult.announceMessage, userRoleList);
        ChatRequest request = ChatRequest.createSystemRequest(announceMessage);
        GroupManager.broadcastMessage(request);
    }

    private static GameResult getGameResult() {
        int mafiaAliveCount = 0;
        int elseAliveCount = 0;
        for (PlayerHandler p : playerGroup) {
            if (p.isAlive() && (p.getRole() == Role.MAFIA)) {
                mafiaAliveCount++;
            } else if (p.isAlive()) {
                elseAliveCount++;
            }
        }
        if (mafiaAliveCount > 0 && elseAliveCount < 2) {
            return GameResult.MAFIA_WIN;
        } else if (mafiaAliveCount == 0) {
            return GameResult.CITIZEN_WIN;
        } else {
            return GameResult.RESUME;
        }
    }
}
