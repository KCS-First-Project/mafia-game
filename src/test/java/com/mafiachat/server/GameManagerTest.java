package com.mafiachat.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.server.handler.ClientHandler;
import com.mafiachat.server.handler.Player.Player;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameManagerTest {

    @Mock
    ChatServer chatServer;
    @Mock
    private Thread gameThread;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Socket clientSocket;

    @Mock
    private GroupManager groupManager;

    private GameManager gameManager;

    private Player player;
    private final List<ClientHandler> mockClients = new ArrayList<>();

    @Captor
    ArgumentCaptor<ChatRequest> requestArgumentCaptor;

    @Captor
    ArgumentCaptor<List<ClientHandler>> clientHandlerArgumentCaptor;

    private Logger logger = Logger.getLogger(GameManagerTest.class.getSimpleName());


    @BeforeEach
    void setUp() {
        gameManager = GameManager.getInstance();
        mockClients.forEach((c) -> groupManager.addClientHandler(c));
        player = spy(new Player(gameManager));
    }

    @BeforeEach
    public void runServer() throws IOException {
        chatServer = new ChatServer(GroupManager.getInstance(), GameManager.getInstance());
        gameThread = new Thread(chatServer);
        gameThread.start();
    }

    @AfterEach
    public void afterWork() {
        gameManager.getRole2TargetPlayer().clear();
        gameManager.clearVoteCount();
        mockClients.clear();
        chatServer.shutdownHook();
    }


    @DisplayName("플레이어가 5인 미만일 때 게임은 시작되지 않는다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @ValueSource(ints = {1, 2, 3, 4})
    public void shouldNotStartGameWhenPlayerCountIsLessThanFive(int playerCnt) {
        //given
        List<Integer> ids = getRandomIds(playerCnt);
        List<PlayerHandler> playerHandlers = ids.stream()
                .map((id) -> {
                    PlayerHandler mockPlayerHandler = mock();
                    gameManager.addPlayerHandler(mockPlayerHandler);
                    return mockPlayerHandler;
                })
                .toList();

        //when
        boolean result = gameManager.tryStartGame();

        //then
        assertEquals(false, result);
        for (PlayerHandler handler : playerHandlers) {
            gameManager.removePlayerHandler(handler);
        }
    }

    @DisplayName("모든 유저가 Ready를 하지 않는 경우 게임은 시작되지 않는다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @ValueSource(ints = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17})
    public void shouldNotStartGameWhenPlayerNotAllReady(int playerCnt) throws IOException {
        //given
        List<Integer> ids = getRandomIds(playerCnt);
        List<PlayerHandler> playerHandlers = ids.stream()
                .map((id) -> {
                    PlayerHandler mockPlayerHandler = mock();
                    gameManager.addPlayerHandler(mockPlayerHandler);
                    return mockPlayerHandler;
                })
                .toList();

        //when
        boolean result = gameManager.tryStartGame();

        //then
        assertEquals(false, result, "아직 준비되지 않은 플레이어가 있습니다.");
        for (PlayerHandler handler : playerHandlers) {
            gameManager.removePlayerHandler(handler);
        }
    }

    @DisplayName("모든 유저가 Ready를 한 경우 게임은 시작된다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @ValueSource(ints = {5, 6, 7, 8})
    public void shouldStartGameWhenAllPlayersAreReady(int playerCnt) throws IOException {
        //given
        List<Integer> ids = getRandomIds(playerCnt);
        List<PlayerHandler> playerHandlers = ids.stream()
                .map((id) -> {
                    PlayerHandler mockPlayerHandler = mock();
                    when(mockPlayerHandler.isReady()).thenReturn(true);
                    gameManager.addPlayerHandler(mockPlayerHandler);
                    return mockPlayerHandler;
                })
                .toList();

        //when
        boolean result = gameManager.tryStartGame();

        //then
        assertEquals(true, result);
        for (PlayerHandler handler : playerHandlers) {
            gameManager.removePlayerHandler(handler);
        }
    }

    @DisplayName("아이디별 투표 집계가 올바르게 이루어지는지 테스트")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @ValueSource(ints = {100, 200, 300})
    void voteCountPerId(int voteCnt) {
        //given
        int id = 1;

        //when
        for (int i = 0; i < voteCnt; i++) {
            gameManager.vote(id);
        }

        //then
        assertThat(gameManager.getVoteCountById(id)).isEqualTo(voteCnt);
    }

    @DisplayName("가장 많은 투표를 받은 플레이어 아이디 반환")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @CsvSource({"1,10,2,15", "3,20,4,25", "5,30,6,35"})
    public void getMostVotedPlayerIds(int id1, int voteCount1, int id2, int voteCount2) {
        //given
        PlayerHandler mockPlayerHandler1 = mock();
        when(mockPlayerHandler1.getId()).thenReturn(id1);
        gameManager.addPlayerHandler(mockPlayerHandler1);

        PlayerHandler mockPlayerHandler2 = mock();
        when(mockPlayerHandler2.getId()).thenReturn(id2);
        gameManager.addPlayerHandler(mockPlayerHandler2);

        //when
        for (int i = 0; i < voteCount1; i++) {
            gameManager.vote(id1);
        }
        for (int i = 0; i < voteCount2; i++) {
            gameManager.vote(id2);
        }

        List<Integer> mostVotedPlayerIds = gameManager.getMostVotedPlayerIds();

        //then
        assertThat(mostVotedPlayerIds).containsOnly(id2);
        gameManager.removePlayerHandler(mockPlayerHandler1);
        gameManager.removePlayerHandler(mockPlayerHandler2);
    }

    @DisplayName("시민이 아닌 직업을 가진 플레이어 타깃팅는 다른 플레이어를 타깃팅 할 수 있다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @EnumSource(value = Role.class, names = {"MAFIA", "DOCTOR", "POLICE"})
    public void setTargetPlayer(Role role) throws IOException {
        //given
        PlayerHandler playerHandler = new PlayerHandler(groupManager, gameManager, clientSocket, player);
        gameManager.addPlayerHandler(playerHandler);

        //when
        gameManager.setTargetPlayer(role, playerHandler);

        //then
        PlayerHandler actualTargetPlayer = gameManager.getRole2TargetPlayer().get(role);
        assertSame(playerHandler, actualTargetPlayer);
    }

    @DisplayName("시민인 경우 다른 플레이어를 타깃팅 할 수 없다.")
    @Test
    public void cannotTargetPlayerWhenRoleIsCitizen() throws IOException {
        //given
        PlayerHandler playerHandler = new PlayerHandler(groupManager, gameManager, clientSocket, player);
        gameManager.addPlayerHandler(playerHandler);
        Role role = Role.CITIZEN;

        //when
        gameManager.setTargetPlayer(role, playerHandler);

        //then
        assertNull(gameManager.getRole2TargetPlayer().get(role));
    }

    @DisplayName("죽은 사람을 타깃팅 할 수 없다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @EnumSource(value = Role.class, names = {"MAFIA", "DOCTOR", "POLICE"})
    public void cannotTargetPlayerWhenTargetPlayerIsNotAlive(Role role) throws IOException {
        //given
        PlayerHandler playerHandler = new PlayerHandler(groupManager, gameManager, clientSocket, player);
        gameManager.addPlayerHandler(playerHandler);
        player.killInGame();

        //when
        gameManager.setTargetPlayer(role, playerHandler);

        //then
        assertNull(gameManager.getRole2TargetPlayer().get(role));
    }

    @DisplayName("이미 타켓팅이 된 경우 다른 플레이어는 타켓팅 할 수 없다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @EnumSource(value = Role.class, names = {"MAFIA", "DOCTOR", "POLICE"})
    public void cannotTargetPlayerWhenAlreadyTargeted(Role role) throws IOException {
        //given
        PlayerHandler targetPlayer = new PlayerHandler(groupManager, gameManager, clientSocket, player);
        gameManager.addPlayerHandler(targetPlayer);

        //when
        gameManager.setTargetPlayer(role, targetPlayer);

        //then
        assertEquals(targetPlayer, gameManager.getRole2TargetPlayer().get(role));
    }

    @DisplayName("직업이 시민인 플레이어는 밤에 아무런 안내 메시지를 받지 못한다.")
    @Test
    public void verifyNoBroadcastForCitizenRole() {
        //given
        Role role = Role.CITIZEN;
        ChatRequest request = new ChatRequest("Test Message");
        GroupManager mockGroupManager = mock(GroupManager.class);

        //when
        gameManager.broadcastNormalRoleMessage(role, request);

        // Then
        verify(mockGroupManager, never()).multicastMessage(any(ChatRequest.class), anyList());
    }

    @DisplayName("직업이 시민이 아닌 플레이어는 밤에 안내 메시지를 받는다.")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @EnumSource(value = Role.class, names = {"MAFIA", "DOCTOR", "POLICE"})
    public void testBroadcastNormalRoleMessage_NotCitizen(Role role) {
        //given
        ChatRequest request = new ChatRequest("[NIGHT]Test Message");
        GroupManager spyGroupManager = spy();
        doNothing().when(spyGroupManager).multicastMessage(any(), any());
        GroupManager tmpGroupManager = groupManager;
        gameManager.setGroupManager(spyGroupManager);

        PlayerHandler mockPlayerHandler1 = mock();
        when(mockPlayerHandler1.getRole()).thenReturn(role);
        gameManager.addPlayerHandler(mockPlayerHandler1);
        PlayerHandler mockPlayerHandler2 = mock();
        when(mockPlayerHandler2.getRole()).thenReturn(role);
        gameManager.addPlayerHandler(mockPlayerHandler2);

        List<ClientHandler> receivers = Arrays.asList(mockPlayerHandler1, mockPlayerHandler2);

        //when
        gameManager.broadcastNormalRoleMessage(role, request);

        //then
        verify(spyGroupManager, times(1)).multicastMessage(requestArgumentCaptor.capture(),
                clientHandlerArgumentCaptor.capture());
        assertEquals(request, requestArgumentCaptor.getValue());
        assertEquals(receivers, clientHandlerArgumentCaptor.getValue());

        gameManager.removePlayerHandler(mockPlayerHandler1);
        gameManager.removePlayerHandler(mockPlayerHandler2);
        gameManager.setGroupManager(tmpGroupManager);
    }

    private List<Integer> getRandomIds(int size) {
        List<Integer> ids = new ArrayList<>(IntStream
                .range(1, 100)
                .boxed()
                .toList());
        Collections.shuffle(ids);
        return ids.subList(0, size);
    }
}
