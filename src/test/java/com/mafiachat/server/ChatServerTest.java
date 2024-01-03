package com.mafiachat.server;

import com.mafiachat.protocol.Command;
import java.io.IOException;
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
        chatServer = new ChatServer();
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
        //given
//        @beforeEach, @AfterEach

        //then
        logger.info("command = " + command);
    }
}
