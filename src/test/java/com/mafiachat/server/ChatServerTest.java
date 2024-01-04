package com.mafiachat.server;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import com.mafiachat.util.Constant;
import com.mafiachat.util.TestClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatServerTest {
    
    private ChatServer chatServer;
    @Mock
    Thread serverThread;

    private Logger logger = Logger.getLogger(ChatServerTest.class.getSimpleName());

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

    @DisplayName("Client 서버 연결 테스트")
    @ParameterizedTest(name = "{index} {displayName} arguments = {arguments} message = {0}")
    @EnumSource(value = Command.class, names = {"INIT_ALIAS", "NORMAL", "SYSTEM"})
    public void connect_client(Command command) {
        //then
        logger.info("command = " + command);
    }

    public static List<TestClient> connectClients(int userNumber) throws IOException {
        ArrayList<TestClient> clients = new ArrayList<>();
        for (int i = 0; i < userNumber; i++) {
            TestClient client = connectClient("user%s".formatted(i + 1));
            clients.add(client);
        }
        return clients;
    }

    public static TestClient connectClient(String alias) throws IOException {
        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
        ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, alias);
        testClient.write(request.getFormattedMessage());
        return testClient;
    }
}
