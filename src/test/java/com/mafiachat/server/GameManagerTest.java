package test.java.com.mafiachat.server;

import java.io.IOException;
import java.util.List;
import main.java.com.mafiachat.server.manager.GameManager;
import org.junit.jupiter.api.Test;
import test.java.com.mafiachat.util.TestClient;

public class GameManagerTest {
    @Test
    public void testTryStartGame() throws InterruptedException, IOException {
        ChatServerTest.runServer();
        List<TestClient> clients = ChatServerTest.connectClients(5);
        while (!GameManager.tryStartGame()) {
            GameManager.delay(1000);
            GameManager.setAllPlayersReady();
        }
        GameManager.getGameThread().join();
    }
}
