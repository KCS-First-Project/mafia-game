package com.mafiachat.server;

import java.io.IOException;
import java.util.List;
import com.mafiachat.server.manager.GameManager;
import org.junit.jupiter.api.Test;
import com.mafiachat.util.TestClient;

public class GameManagerTest {
    @Test
    public void testTryStartGame() throws InterruptedException, IOException {
        GameManager gameManager = GameManager.getInstance();
        ChatServerTest.runServer();
        List<TestClient> clients = ChatServerTest.connectClients(5);
        while (!gameManager.tryStartGame()) {
            gameManager.delay(1000);
            gameManager.setAllPlayersReady();
        }
        gameManager.getGameThread().join();
    }
}