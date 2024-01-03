package com.mafiachat.server;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;

public class GameManagerTest {

//    @Test
//    public void testTryStartGame() throws InterruptedException, IOException {
//        runServer();
//        List<TestClient> clients = ChatServerTest.connectClients(5);
//        while (!GameManager.tryStartGame()) {
//            GameManager.delay(1000);
//            GameManager.setAllPlayersReady();
//        }
//        GameManager.getGameThread().join();
//    }

    @BeforeEach
    public void runServer() throws IOException {
        Thread serverThread = new Thread(new ChatServer());
        serverThread.start();
    }
}
