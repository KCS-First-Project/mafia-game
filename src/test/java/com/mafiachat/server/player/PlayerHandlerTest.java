package com.mafiachat.server.player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.handler.Player.Player;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PlayerHandlerTest {
    @ParameterizedTest
    @MethodSource("getTestRequest")
    public void testProcessRequest(ChatRequest request) throws IOException {
        GroupManager groupManager = mock(GroupManager.class);
        GameManager gameManager = mock(GameManager.class);
        Player player = spy(new Player(gameManager));
        Socket clientSocket = mock(Socket.class, RETURNS_DEEP_STUBS);
        when(clientSocket.getInetAddress().getHostAddress()).thenReturn("127.0.0.1");
        when(clientSocket.getOutputStream()).thenReturn(System.out);
        when(clientSocket.getInputStream()).thenReturn(System.in);

        PlayerHandler playerHandler = new PlayerHandler(groupManager, gameManager, clientSocket, player);
        playerHandler.processRequest(request);

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

    public static Stream<Arguments> getTestRequest(){
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
}
