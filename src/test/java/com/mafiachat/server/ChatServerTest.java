package com.mafiachat.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.server.ChatServer;
import com.mafiachat.util.Constant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.mafiachat.util.TestClient;

public class ChatServerTest {
    @Test
    public void testConnectClient() throws IOException {
        //given
        runServer();
        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
        ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, "aaaa");
        //when
        testClient.write(request.getFormattedMessage());
        String formattedMessage = testClient.read();
        //then
        Command command = new ChatResponse(formattedMessage).getCommand();
        Assertions.assertEquals(command.name(), Command.USER_LIST.name());
    }

    @Test
    public void testCommunicate() throws IOException {
        runServer();
        TestClient c1 = connectClient("aaa");
        TestClient c2 = connectClient("bbb");
        ChatRequest r1 = ChatRequest.createNormalRequest("aaa", "a");
        ChatRequest r2 = ChatRequest.createNormalRequest("bbb", "b");
        c1.write(r1.getFormattedMessage());
        c2.write(r2.getFormattedMessage());
        System.out.printf("test: %s\n", c1.read());
        System.out.printf("test: %s\n", c1.read());
        System.out.printf("test: %s\n", c1.read());
        System.out.printf("test: %s\n", c1.read());
        System.out.printf("test: %s\n", c1.read());
        System.out.printf("test: %s\n", c1.read());

        System.out.println(c1.read());
        System.out.println(c1.read());
        System.out.println(c1.read());
        System.out.println(c1.read());
        System.out.println(c1.read());
        System.out.println(c2.read());
        System.out.println(c2.read());
        System.out.println(c2.read());
    }

    public static void runServer() throws IOException {
        Thread serverThread = new Thread(new ChatServer());
        serverThread.start();
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
