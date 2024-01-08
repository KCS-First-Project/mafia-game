package com.mafiachat.server.manager;

import com.mafiachat.exception.MaxPlayerException;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.handler.ClientHandler;

import static com.mafiachat.util.Constant.MAX_PLAYER_NUMBER;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupManagerTest {
    private GroupManager groupManager;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    ArgumentCaptor<ChatRequest> requestArgumentCaptor;
    @Captor
    ArgumentCaptor<List<ClientHandler>> clientsArgumentCaptor;

    private final List<ClientHandler> mockClients = new ArrayList<>();
    private final String mockIp = "127.0.0.1";

    @BeforeEach
    public void beforeEach() {
        groupManager = GroupManager.getInstance();
        mockClients.forEach((c) -> groupManager.addClientHandler(c));
    }

    @AfterEach
    public void afterEach() {
        groupManager = GroupManager.getInstance();
        groupManager.closeAllClientHandlers();
        mockClients.clear();
    }

    @ParameterizedTest
    @DisplayName("클라 id 자동 생성 테스트")
    @MethodSource("getTestcaseForCreateClientId")
    public void testCreateClientId(int expectedId) {
        int id = groupManager.createClientId();
        assertThat(id).isEqualTo(expectedId);
    }

    @ParameterizedTest
    @DisplayName("id로 클라 핸들러 찾기 테스트")
    @MethodSource("getTestcaseForFindClientById")
    public void testFindClientById(int id, ClientHandler expectedClient) {
        ClientHandler client = groupManager.findClientById(id);
        assertThat(client).isEqualTo(expectedClient);
    }

    @ParameterizedTest
    @DisplayName("id로 클라 핸들러 이름 찾기 테스트")
    @MethodSource("getTestcaseForFindClientNameById")
    public void testFindClientNameById(int id, String expectedClientName) {
        String clientName = groupManager.findClientNameById(id);
        assertThat(clientName).isEqualTo(expectedClientName);
    }

    @Test
    @DisplayName("클라 핸들러 추가 테스트")
    public void testAddClientHandler() {
        ClientHandler excessClient = mock(ClientHandler.class);

        Stream.generate(() -> mock(ClientHandler.class))
                .limit(MAX_PLAYER_NUMBER)
                .forEach((c) -> groupManager.addClientHandler(c));

        assertThatThrownBy(() -> {
            groupManager.addClientHandler(excessClient);
        }).isInstanceOf(MaxPlayerException.class);
    }

    @Test
    @DisplayName("클라 핸들러 제거 테스트")
    public void testRemoveClientHandler() {
        GroupManager spyGroupManager = spy(groupManager);
        doNothing().when(spyGroupManager).broadcastMessage(isA(ChatRequest.class));
        ClientHandler mockClient = mock();
        spyGroupManager.addClientHandler(mockClient);

        spyGroupManager.removeClientHandler(mockClient);

        verify(spyGroupManager, times(1)).broadcastMessage(isA(ChatRequest.class));
    }

    @Test
    @DisplayName("멀티캐스트 테스트")
    public void testMulticastMessage() {
        List<Integer> ids = getRandomIds(5);
        List<ClientHandler> mockClients = ids.stream()
                .map((id) -> {
                    ClientHandler mockClient = mock();
                    doNothing().when(mockClient).sendMessage(isA(String.class));
                    return mockClient;
                })
                .toList();

        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, "hello");
        groupManager.multicastMessage(request, mockClients);

        mockClients.forEach((mockClient) -> {
            verify(mockClient, times(1)).sendMessage(stringArgumentCaptor.capture());
            String arg = stringArgumentCaptor.getValue();
            assertThat(arg).isEqualTo(request.getFormattedMessage());
        });
    }

    @Test
    @DisplayName("유니캐스트 테스트")
    public void testUnicastMessage() {
        ClientHandler mockClient = mock();
        doNothing().when(mockClient).sendMessage(isA(String.class));

        ChatRequest request = ChatRequest.createRequest(Command.NORMAL, "hello");
        groupManager.unicastMessage(request, mockClient);

        verify(mockClient, times(1)).sendMessage(stringArgumentCaptor.capture());
        String arg = stringArgumentCaptor.getValue();
        assertThat(arg).isEqualTo(request.getFormattedMessage());
    }

    @ParameterizedTest
    @DisplayName("유저 리스트 노티 테스트")
    @MethodSource("getTestcaseForNotifyUserList")
    public void testNotifyUserList(String expectedBody) {
        GroupManager spyGroupManager = spy(groupManager);
        doNothing().when(spyGroupManager).broadcastMessage(isA(ChatRequest.class));

        spyGroupManager.notifyUserList();

        verify(spyGroupManager, times(1)).broadcastMessage(requestArgumentCaptor.capture());
        String body = requestArgumentCaptor.getValue().getBody();
        assertThat(body).isEqualTo(expectedBody);
    }

    @Test
    @DisplayName("핸들러 모두 종료 테스트")
    public void testCloseAllClientHandlers() {
        IntStream.rangeClosed(0, 5)
                .forEach((int i) -> {
                    ClientHandler mockClient = mock();
                    mockClients.add(mockClient);
                    groupManager.addClientHandler(mockClient);
                });

        groupManager.closeAllClientHandlers();

        for (ClientHandler mockClient : mockClients) {
            verify(mockClient, times(1)).close();
        }
    }

    @ParameterizedTest
    @DisplayName("새로운 핸들러 안내 메시지 테스트")
    @MethodSource("getTestcaseForBroadcastNewChatter")
    public void testBroadcastNewChatter(ClientHandler newHandler, List<ClientHandler> expectedReceivers) {
        GroupManager spyGroupManager = spy(groupManager);
        doNothing().when(spyGroupManager).multicastMessage(isA(ChatRequest.class), anyList());
        doNothing().when(spyGroupManager).notifyUserList();

        spyGroupManager.broadcastNewChatter(newHandler);

        verify(spyGroupManager, times(1)).multicastMessage(isA(ChatRequest.class), clientsArgumentCaptor.capture());
        List<ClientHandler> receivers = clientsArgumentCaptor.getValue();
        assertThat(receivers.stream().map(ClientHandler::getId).toList())
                .isEqualTo(expectedReceivers.stream().map(ClientHandler::getId).toList());
    }

    @ParameterizedTest
    @DisplayName("멀티스레드 테스트")
    @MethodSource("getTestcaseForMultiThread")
    public void testMultiThread(int a) {
        List<ClientHandler> mocks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ClientHandler mc = mock();
            groupManager.addClientHandler(mc);
            mocks.add(mc);
        }
        groupManager.closeAllClientHandlers();
        for (ClientHandler mc : mocks) {
            groupManager.removeClientHandler(mc);
        }
    }

    private Stream<Arguments> getTestcaseForCreateClientId() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4)
        );
    }

    private List<Integer> getRandomIds(int size) {
        List<Integer> ids = new ArrayList<>(IntStream
                .range(1, 100)
                .boxed()
                .toList());
        Collections.shuffle(ids);
        return ids.subList(0, size);
    }

    private Stream<Arguments> getTestcaseForFindClientById() {
        List<Integer> ids = getRandomIds(6);
        return ids.stream()
                .map((id) -> {
                    ClientHandler mockClient = mock();
                    when(mockClient.getId()).thenReturn(id);
                    mockClients.add(mockClient);
                    return Arguments.of(id, mockClient);
                });
    }

    private Stream<Arguments> getTestcaseForFindClientNameById() {
        List<Integer> ids = getRandomIds(6);
        return ids.stream()
                .map((id) -> {
                    ClientHandler mockClient = mock();
                    when(mockClient.getId()).thenReturn(id);
                    when(mockClient.getClientName()).thenReturn("mock" + id);
                    mockClients.add(mockClient);
                    return Arguments.of(id, mockClient.getClientName());
                });
    }

    private Stream<Arguments> getTestcaseForNotifyUserList() {
        List<Integer> ids = getRandomIds(6);
        List<String> users = new ArrayList<>();
        ids.forEach((id) -> {
            ClientHandler mockClient = mock(ClientHandler.class);
            when(mockClient.getId()).thenReturn(id);
            when(mockClient.getClientName()).thenReturn("mock" + id);
            when(mockClient.getFrom()).thenReturn(mockIp);
            mockClients.add(mockClient);
            users.add("%d,mock%d,%s".formatted(id, id, mockIp));
        });
        return Stream.of(
                Arguments.of(String.join("|", users))
        );
    }

    public Stream<Arguments> getTestcaseForBroadcastNewChatter() {
        List<Integer> ids = getRandomIds(6);
        int newHandlerId = ids.get(0);
        ClientHandler newHandler = mock();
        when(newHandler.getId()).thenReturn(newHandlerId);
        when(newHandler.getClientName()).thenReturn("mock" + newHandlerId);
        mockClients.add(newHandler);

        List<ClientHandler> expectedReceivers = new ArrayList<>();
        ids.forEach((id) -> {
            if (id != newHandlerId) {
                ClientHandler mockClient = mock();
                when(mockClient.getId()).thenReturn(id);
                expectedReceivers.add(mockClient);
                mockClients.add(mockClient);
            }
        });
        return Stream.of(
                Arguments.of(newHandler, expectedReceivers)
        );
    }

    public Stream<Arguments> getTestcaseForMultiThread() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4),
                Arguments.of(5),
                Arguments.of(6)
        );
    }
}
