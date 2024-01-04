package com.mafiachat.server.handler.Player;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.ChatServer;
import com.mafiachat.server.manager.GameManager;
import java.io.IOException;

import com.mafiachat.server.manager.GroupManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

class PlayerTest {
    @Mock
    ChatServer chatServer;

    @Mock
    Thread serverThread;
    private Player player;

    public PlayerTest() {
        player = new Player(GameManager.getInstance());
    }

    @BeforeEach
    public void runServer() throws IOException {
        chatServer = new ChatServer(GroupManager.getInstance(), GameManager.getInstance());
        serverThread = new Thread(chatServer);
        serverThread.start();
    }

    @AfterEach
    public void stopServer() {
        chatServer.shutdownHook();
    }

    @DisplayName("Client 투표 테스트")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @ValueSource(ints = {1, 2, 3})
    public void vote(int voteCount) {
        //given
        ChatRequest request = ChatRequest.createRequest(Command.VOTE, "1");

        //when
        player.vote(request);
//        doCallRealMethod().when(player).vote(isA(ChatRequest.class));
//        player.vote(request);

        //then
        Assertions.assertEquals(voteCount, GameManager.getInstance().getVoteCountById(1));
    }
}