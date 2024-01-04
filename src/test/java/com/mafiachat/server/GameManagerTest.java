package com.mafiachat.server;

import java.io.IOException;
import java.util.List;

import com.mafiachat.server.manager.GameManager;
import com.mafiachat.server.manager.GroupManager;
import com.mafiachat.util.TestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameManagerTest {

    @Test
    public void testTryStartGame() throws InterruptedException, IOException {
        List<TestClient> clients = ChatServerTest.connectClients(5);
        while (!GameManager.getInstance().tryStartGame()) {
            GameManager.getInstance().delay(1000);
            GameManager.getInstance().setAllPlayersReady();
        }
        GameManager.getInstance().getGameThread().join();
    }

    @BeforeEach
    public void runServer() throws IOException {
        Thread serverThread = new Thread(new ChatServer(GroupManager.getInstance(), GameManager.getInstance()));
        serverThread.start();
    }
}
