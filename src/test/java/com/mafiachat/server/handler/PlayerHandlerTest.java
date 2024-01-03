package com.mafiachat.server.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.ChatServer;
import com.mafiachat.util.Constant;
import com.mafiachat.util.TestClient;
import java.io.IOException;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerHandlerTest {
    @Mock
    ChatServer chatServer;

    @Mock
    Thread serverThread;

    private PlayerHandler playerHandler;

    private Logger logger = Logger.getLogger(PlayerHandlerTest.class.getSimpleName());


    public PlayerHandlerTest() throws IOException {
        playerHandler = new PlayerHandler(); //TODO socket 받기
    }

    @BeforeEach
    public void runServer() throws IOException {
        chatServer = new ChatServer();
        serverThread = new Thread(chatServer);
        serverThread.start();
    }

    @AfterEach
    public void stopServer() {
        chatServer.shutdownHook();
    }

    @DisplayName("Client 기본 대화 테스트")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @ValueSource(strings = {"안녕? 누가 범인이야?", "일단 난 아님!", "태우가 범인 같애!"})
    public void chat_with_others(String talk) throws IOException {
        //given
        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, talk);

        //when
        playerHandler.processRequest(request);
        testClient.write(request.getFormattedMessage());
        String formattedMessage = testClient.read();

        //then
        Command responseCommand = new ChatResponse(formattedMessage).getCommand();
        Assertions.assertEquals(responseCommand.name(), Command.NORMAL.name());
        assertThat(formattedMessage).contains(talk);

//        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
//        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, talk);
//        PlayerHandler playerHandler = mock(PlayerHandler.class);
//        Player player = mock(Player.class);
//
//        //when
//        doNothing().when(playerHandler).processRequest(request);
//        testClient.write(request.getFormattedMessage());
//        String formattedMessage = testClient.read();
//
//        //then
//        Command responseCommand = new ChatResponse(formattedMessage).getCommand();
//        Assertions.assertAll((
//                        () -> Assertions.assertEquals(responseCommand.name(), Command.NORMAL.name())),
//                () -> assertThat(formattedMessage).contains(talk),
//                () -> verify(playerHandler, times(1)).processRequest(request),
//                () -> verify(player, times(1)).talk(request)
//        );
    }

    @DisplayName("Client 닉네임 바꾸기 테스트")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(strings = {"cat", "duck", "dog"})
    public void change_nickname(String nickname) throws IOException {
        //given
        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
        ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, nickname);

        //when
        playerHandler.processRequest(request);
        testClient.write(request.getFormattedMessage());
        String formattedMessage = testClient.read();

        //then
        Command responseCommand = new ChatResponse(formattedMessage).getCommand();
        Assertions.assertEquals(responseCommand.name(), Command.USER_LIST.name());
        assertThat(formattedMessage).contains(nickname);
    }

//    @Mock
//    Player player;

    @DisplayName("Client Ready 테스트")
    @Test
    public void player_get_ready() throws IOException {
        //given
//        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
        ChatRequest request = ChatRequest.createRequest(Command.READY, "");

        //when

        playerHandler.processRequest(request);
//        testClient.write(request.getFormattedMessage());
//        String formattedMessage = testClient.read();

        //then
        Assertions.assertTrue(playerHandler.isReady());

//        Command responseCommand = new ChatResponse(formattedMessage).getCommand();
//        Assertions.assertEquals(responseCommand.name(), Command.USER_LIST.name());
//        assertThat(formattedMessage).contains(nickname);
    }


}