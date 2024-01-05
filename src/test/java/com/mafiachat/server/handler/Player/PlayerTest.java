package com.mafiachat.server.handler.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.ChatServer;
import com.mafiachat.server.Phase;
import com.mafiachat.server.Role;
import com.mafiachat.server.handler.PlayerHandler;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerTest {
    @Mock
    ChatServer chatServer;

    @Mock
    Thread serverThread;

    @Mock
    PlayerHandler playerHandler;

    @Mock
    private GameManager gameManager;

    private Player player;

    @BeforeEach
    public void runServer() throws IOException {
        chatServer = new ChatServer(GroupManager.getInstance(), GameManager.getInstance());
        serverThread = new Thread(chatServer);
        serverThread.start();
    }

    @BeforeEach
    public void setup() throws IOException {
        player = spy(new Player(gameManager));
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

        //then
        assertEquals(voteCount, GameManager.getInstance().getVoteCountById(1));
    }

    @DisplayName("밤에 시민이 아닌 역할들 대화 테스트")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @EnumSource(value = Role.class, names = {"MAFIA", "DOCTOR", "POLICE", "CITIZEN"})
    public void talk_at_night(Role role) {
        //given
        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, "Test message");
        player.setRole(role);

        //when
        player.talk(request);

        //then
        if ((gameManager.getPhase() == Phase.NIGHT) && (role != Role.CITIZEN)) {
            verify(gameManager, times(1)).broadcastNormalRoleMessage(role, request);
            verify(gameManager, never()).broadcastMessage(request);
        } else {
            verify(gameManager, never()).broadcastNormalRoleMessage(role, request);
            verify(gameManager, times(1)).broadcastMessage(request);
        }
    }

    @Captor
    ArgumentCaptor<Role> roleCaptor;

    @Captor
    ArgumentCaptor<PlayerHandler> playerHandlerCaptor;

    @DisplayName("각 직업별 역할 테스트")
    @ParameterizedTest(name = "{index} {displayName} message = {0}")
    @EnumSource(value = Role.class, names = {"MAFIA", "DOCTOR", "POLICE", "CITIZEN"})
    public void targetPlayer_test(Role role) {
        //given
        int id = 1;
        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, String.valueOf(id));
        player.setRole(role);

        //when
        player.targetPlayer(request, role);

        //then
        verify(gameManager).setTargetPlayer(roleCaptor.capture(), playerHandlerCaptor.capture());

        // 인자값 검증
        Role capturedRole = roleCaptor.getValue();
        PlayerHandler capturedPlayerHandler = playerHandlerCaptor.getValue();

        // 예상한 Role과 PlayerHandler가 실제로 setTargetPlayer 메서드에 전달되었는지 검증
        assertEquals(role, capturedRole);
        assertEquals(gameManager.findPlayerById(id), capturedPlayerHandler);
    }
}