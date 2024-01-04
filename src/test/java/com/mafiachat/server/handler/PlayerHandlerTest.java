package com.mafiachat.server.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.ChatServer;
import com.mafiachat.server.handler.Player.Player;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import com.mafiachat.util.Constant;
import com.mafiachat.util.TestClient;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerHandlerTest {
    private ChatServer chatServer;

    @Mock
    private Thread serverThread;

    private PlayerHandler playerHandler;

    private Player player;

    @Mock
    private GroupManager groupManager;

    @Mock
    private GameManager gameManager;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Socket clientSocket;

    private TestClient testClient;

    private Logger logger = Logger.getLogger(PlayerHandlerTest.class.getSimpleName());

    @AfterEach
    public void stopServer() {
        chatServer.shutdownHook();
    }

    @BeforeEach
    public void runServer() throws IOException {
        chatServer = new ChatServer(GroupManager.getInstance(), GameManager.getInstance());
        serverThread = new Thread(chatServer);
        serverThread.start();
    }

    @BeforeEach
    public void setup() throws IOException {
        player = spy(new Player(gameManager));
        when(clientSocket.getInetAddress().getHostAddress()).thenReturn("127.0.0.1");
        when(clientSocket.getOutputStream()).thenReturn(System.out);
        when(clientSocket.getInputStream()).thenReturn(System.in);
        playerHandler = new PlayerHandler(groupManager, gameManager, clientSocket, player);
        testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
    }

    @DisplayName("Client 기본 대화 테스트")
    @ParameterizedTest(name = "{index} {displayName}")
    @MethodSource("getTestRequest")
    public void testProcessRequest(ChatRequest request) {
        //when
        playerHandler.processRequest(request);

        //then
        Command command = request.getCommand();
        switch (command) {
            case NORMAL:
                verify(player, times(1)).talk(request);
                break;
            case INIT_ALIAS:
                verify(player, times(1)).setChatName(request.getBody());
                verify(groupManager, times(1)).broadcastNewChatter(playerHandler);
                break;
            case READY:
                playerHandler.setReady();
                Assertions.assertTrue(player.isReady());
                verify(gameManager, times(1)).tryStartGame();
                break;
            case VOTE:
                verify(player, times(1)).vote(request);
                break;
            case ACT_ROLE:
                verify(player, times(1)).targetPlayer(request, player.getRole());
                break;
        }
    }

    public static Stream<Arguments> getTestRequest() {
        ChatRequest initAliasRequest = ChatRequest.createAliasInitRequest("aaa");
        ChatRequest normalRequest = ChatRequest.createNormalRequest("aaa", "im aaa");
        ChatRequest readyRequest = ChatRequest.createRequest(Command.READY, "");
        ChatRequest voteRequest = ChatRequest.createRequest(Command.VOTE, "1");
        ChatRequest actRoleRequest = ChatRequest.createRequest(Command.ACT_ROLE, "1");

        return Stream.of(
                Arguments.of(initAliasRequest),
                Arguments.of(normalRequest),
                Arguments.of(readyRequest),
                Arguments.of(voteRequest),
                Arguments.of(actRoleRequest)
        );
    }

    @DisplayName("Client 기본 대화 테스트")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @ValueSource(strings = {"안녕? 누가 범인이야?", "일단 난 아님!", "태우가 범인 같애!"})
    public void chat_with_others(String talk) throws IOException {
        //given
        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, talk);

        //when
        String formattedMessage = getFormattedMessage(request);

        //then
        Command responseCommand = new ChatResponse(formattedMessage).getCommand();
        Assertions.assertEquals(responseCommand.name(), Command.NORMAL.name());
        assertThat(formattedMessage).contains(talk);
    }

    @DisplayName("Client 닉네임 바꾸기 테스트")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(strings = {"cat", "duck", "dog"})
    public void change_nickname(String nickname) throws IOException {
        //given
        ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, nickname);

        //when
        String formattedMessage = getFormattedMessage(request);

        //then
        Command responseCommand = new ChatResponse(formattedMessage).getCommand();
        Assertions.assertEquals(responseCommand.name(), Command.USER_LIST.name());
        assertThat(formattedMessage).contains(nickname);
    }

    private String getFormattedMessage(ChatRequest request) throws IOException {
        playerHandler.processRequest(request);
        testClient.write(request.getFormattedMessage());
        String formattedMessage = testClient.read();
        return formattedMessage;
    }
    
    @DisplayName("Client Ready 테스트")
    @Test
    public void player_get_ready() {
        //given
        ChatRequest request = ChatRequest.createRequest(Command.READY, "");

        //when
        playerHandler.processRequest(request);

        //then
        Assertions.assertTrue(playerHandler.isReady());
    }
}