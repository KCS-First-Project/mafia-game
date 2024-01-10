package com.mafiachat.server.manager;


import static com.mafiachat.util.Constant.MIN_PLAYER_NUMBER;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.GameResult;
import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import com.mafiachat.server.RoleAssignment;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.PlayerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameManager {
    private GroupManager groupManager;
    private Thread gameThread = null;
    private Phase phase = Phase.LOBBY;
    private final List<PlayerHandler> playerGroup = Collections.synchronizedList(new ArrayList<>());
    private final HashMap<Integer, Integer> id2VoteCount = new HashMap<>();
    private final HashMap<Role, PlayerHandler> role2TargetPlayer = new HashMap<>();
    private final Logger logger = Logger.getLogger(GameManager.class.getSimpleName());

    private GameManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public static GameManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public HashMap<Role, PlayerHandler> getRole2TargetPlayer() {
        return role2TargetPlayer;
    }

    public void clearRole2TargetPlayer() {
        role2TargetPlayer.clear();
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    synchronized public void addPlayerHandler(PlayerHandler handler) {
        playerGroup.add(handler);
    }

    synchronized public void removePlayerHandler(PlayerHandler handler) {
        playerGroup.remove(handler);
    }

    synchronized public boolean tryStartGame() {
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

    public Phase getPhase() {
        return phase;
    }

    public void delay(int milliSeconds) throws InterruptedException {
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

    synchronized public void vote(int id) {
        int voteCount = id2VoteCount.getOrDefault(id, 0);
        id2VoteCount.put(id, ++voteCount);
    }

    public int getVoteCountById(int id) {
        return id2VoteCount.getOrDefault(id, 0);
    }

    public void clearVoteCount() {
        id2VoteCount.clear();
    }

    public List<Integer> getMostVotedPlayerIds() {
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

    synchronized public void setTargetPlayer(Role role, PlayerHandler target) {
        if (role == Role.CITIZEN) {
            return;
        }
        if (!target.isAlive()) {
            return;
        }
        PlayerHandler old = role2TargetPlayer.getOrDefault(role, null);
        if (old != null) {
            return;
        }
        role2TargetPlayer.put(role, target);
    }

    public void broadcastMessage(ChatRequest request) {
        groupManager.broadcastMessage(request);
    }

    public void broadcastNormalRoleMessage(Role role, ChatRequest request) {
        if (role == Role.CITIZEN) {
            return;
        }
        List<ClientHandler> players = findPlayersByRole(role);
        groupManager.multicastMessage(request, players);
    }

    public Thread getGameThread() {
        return gameThread;
    }

    public PlayerHandler findPlayerById(int id) {
        return (PlayerHandler) groupManager.findClientById(id);
    }

    private void startGame() throws InterruptedException {
        assignRole();
        announceRole();
        phase = Phase.LOBBY;
        clearVoteCount();
        clearRole2TargetPlayer();

        logger.log(Level.INFO, "Game started");
        notifyPlayerList();
        startDayChat();
    }

    private void startDayChat() throws InterruptedException {
        notifyPlayerList();
        GameResult gameResult = getGameResult();
        if (gameResult != GameResult.RESUME) {
            endGame(gameResult);
        }

        onPhase(Phase.DAY_CHAT);

        startDayFirstVote();
    }

    private void startDayFirstVote() throws InterruptedException {
        clearVoteCount();
        notifyPlayerList();

        onPhase(Phase.DAY_FIRST_VOTE);

        List<Integer> mostVotedPlayerIds = getMostVotedPlayerIds();
        if (mostVotedPlayerIds.isEmpty()) {
            announceKilledPlayer("투표 결과", null);
            startNight();
        } else {
            startDayDefense(mostVotedPlayerIds);
        }
    }

    private void startDayDefense(List<Integer> mostVotedPlayerIds) throws InterruptedException {
        notifyPlayerList();
        String announceMessage = mostVotedPlayerIds.stream()
                .map((id) -> "%s(%d)".formatted(groupManager.findClientNameById(id), id))
                .collect(Collectors.joining(", "));
        ChatRequest votedNamesRequest = ChatRequest.createSystemRequest(announceMessage);
        ChatRequest votedListRequest = ChatRequest.createRequest(
                Command.VOTED_LIST,
                mostVotedPlayerIds.stream()
                        .map("%d"::formatted)
                        .collect(Collectors.joining(","))
        );
        groupManager.broadcastMessage(votedNamesRequest);
        groupManager.broadcastMessage(votedListRequest);

        onPhase(Phase.DAY_DEFENSE);

        startDaySecondVote(mostVotedPlayerIds);
    }

    private void startDaySecondVote(List<Integer> mostVotedPlayerIds) throws InterruptedException {
        clearVoteCount();
        notifyPlayerList();

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

    private void startNight() throws InterruptedException {
        notifyPlayerList();

        onPhase(Phase.NIGHT);

        PlayerHandler killedByMafia = applyRoleAction();
        announceKilledPlayer("밤 사이에", killedByMafia);
        GameResult gameResult = getGameResult();
        if (gameResult == GameResult.RESUME) {
            startNight();
        } else {
            endGame(gameResult);
        }
    }

    private void endGame(GameResult gameResult) {
        notifyPlayerList();
        announceResult(gameResult);
        dismissRole();
        goToLobby();
    }

    private void goToLobby() {
        phase = Phase.LOBBY;
        ChatRequest phaseNotifyRequest = ChatRequest.createRequest(Command.valueOf(phase.name()), "");
        groupManager.broadcastMessage(phaseNotifyRequest);
    }

    private boolean checkAllReady() {
        Optional<Boolean> isAllReady = playerGroup.stream()
                .map(PlayerHandler::isReady)
                .reduce((x, y) -> x && y);
        return isAllReady.isPresent() && isAllReady.get();
    }

    private void onPhase(Phase phase) throws InterruptedException {
        proceedPhase();

        ChatRequest phaseNotifyRequest = ChatRequest.createRequest(Command.valueOf(phase.name()), "");
        groupManager.broadcastMessage(phaseNotifyRequest);
        ChatRequest phaseAnnounceRequest = ChatRequest.createSystemRequest(phase.announceMessage);
        groupManager.broadcastMessage(phaseAnnounceRequest);

        logger.log(Level.INFO, "%s started".formatted(phase.name()));
        delay(phase.timeLimit);
        logger.log(Level.INFO, "%s ended".formatted(phase.name()));
    }

    private void proceedPhase() {
        int nextOrdinal = (phase.ordinal() + 1) % Phase.values().length;
        phase = Phase.values()[nextOrdinal];
    }

    private void assignRole() {
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

    private void dismissRole() {
        playerGroup.forEach((player) -> player.setRole(null));
    }

    private PlayerHandler getPlayerToKill(List<Integer> mostVotedPlayerIds) {
        if (mostVotedPlayerIds.isEmpty()) {
            return null;
        }
        Collections.shuffle(mostVotedPlayerIds);
        int votedPlayerId = mostVotedPlayerIds.get(0);
        return (PlayerHandler) groupManager.findClientById(votedPlayerId);
    }

    private void killVotedPlayer(PlayerHandler player) {
        if (player == null) {
            return;
        }
        player.killInGame();
        notifyPlayerList();
    }

    private void notifyPlayerList() {
        String players = playerGroup.stream().map((player) ->
                "%s,%s,%s,%s".formatted(
                        player.getId(),
                        player.getClientName(),
                        player.getFrom(),
                        player.isAlive()
                )
        ).collect(Collectors.joining("|"));
        ChatRequest request = ChatRequest.createRequest(Command.PLAYER_LIST, players);
        groupManager.broadcastMessage(request);
    }

    private PlayerHandler applyRoleAction() {
        PlayerHandler mafiaTargetPlayer = role2TargetPlayer.getOrDefault(Role.MAFIA, null);
        PlayerHandler doctorTargetPlayer = role2TargetPlayer.getOrDefault(Role.DOCTOR, null);
        PlayerHandler policeTargetPlayer = role2TargetPlayer.getOrDefault(Role.POLICE, null);
        role2TargetPlayer.clear();

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

    private void broadcastSystemRoleMessage(Role role, ChatRequest request) {
        if (role == Role.CITIZEN) {
            return;
        }
        List<ClientHandler> players = findPlayersByRole(role);
        groupManager.multicastMessage(request, players);
    }

    private List<ClientHandler> findPlayersByRole(Role role) {
        return playerGroup.stream()
                .filter((p) -> p.getRole() == role)
                .collect(Collectors.toList());
    }


    private void announceRole() {
        for (PlayerHandler player : playerGroup) {
            Role role = player.getRole();
            ChatRequest roleAnnounceRequest = ChatRequest.createSystemRequest(
                    "당신의 역할은 %s입니다.".formatted(role.description));
            groupManager.unicastMessage(roleAnnounceRequest, player);

            ChatRequest roleNotifyRequest = ChatRequest.createRequest(Command.NOTIFY_ROLE, role.name());
            groupManager.unicastMessage(roleNotifyRequest, player);
        }
    }

    private void announceKilledPlayer(String context, PlayerHandler killedPlayer) {
        String announceMessage;
        if (killedPlayer == null) {
            announceMessage = "%s 아무도 죽지 않았습니다.".formatted(context);
        } else {
            announceMessage = "%s %s(%d) 플레이어가 죽었습니다.".formatted(context, killedPlayer.getClientName(),
                    killedPlayer.getId());
        }
        ChatRequest request = ChatRequest.createSystemRequest(announceMessage);
        groupManager.broadcastMessage(request);
    }

    private void announceResult(GameResult gameResult) {
        List<String> userRoleMessages = playerGroup.stream()
                .map((p) -> "%s(%d) 플레이어는 %s입니다.".formatted(p.getClientName(), p.getId(), p.getRole().description))
                .toList();
        ChatRequest gameResultRequest = ChatRequest.createSystemRequest(gameResult.announceMessage);
        groupManager.broadcastMessage(gameResultRequest);
        for (String userRoleMessage: userRoleMessages) {
            ChatRequest roleRequest = ChatRequest.createSystemRequest(userRoleMessage);
            groupManager.broadcastMessage(roleRequest);
        }
    }

    private GameResult getGameResult() {
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

    private static class LazyHolder {
        private static final GameManager INSTANCE = new GameManager(GroupManager.getInstance());
    }
}
