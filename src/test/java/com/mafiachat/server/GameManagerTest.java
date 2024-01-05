package com.mafiachat.server;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.manager.GameManager;
import com.mafiachat.util.Constant;
import com.mafiachat.util.TestClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class GameManagerTest {
    @Test
    public void testTryStartGame() throws InterruptedException, IOException {
        ChatServerTest.runServer();
        List<TestClient> clients = ChatServerTest.connectClients(5);
        while (!GameManager.tryStartGame()){
            GameManager.delay(1000);
            GameManager.setAllPlayersReady();
        }
        GameManager.getGameThread().join();
    }
}
