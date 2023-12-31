package com.mafiachat.server;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;
import com.mafiachat.protocol.Command;
import com.mafiachat.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.*;

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

    private void runServer() throws IOException {
        Thread serverThread = new Thread(new ChatServer());
        serverThread.start();
    }

    private TestClient connectClient(String alias) throws IOException {
        TestClient testClient = new TestClient(Constant.SERVER_HOST, Constant.SERVER_PORT);
        ChatRequest request = ChatRequest.createRequest(Command.INIT_ALIAS, alias);
        testClient.write(request.getFormattedMessage());
        return testClient;
    }
}
