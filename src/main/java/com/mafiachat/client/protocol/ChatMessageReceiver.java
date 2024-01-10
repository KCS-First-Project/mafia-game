package com.mafiachat.client.protocol;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.event.MessageReceiver;
import com.mafiachat.protocol.ChatResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;


public class ChatMessageReceiver implements Runnable, ChatSocketListener {
    private BufferedReader reader;
    private MessageReceiver receiver;
    ChatConnector connector;
    private Logger logger = Logger.getLogger(ChatMessageReceiver.class.getSimpleName());

    public ChatMessageReceiver(ChatConnector c) {
        connector = c;
    }

    public void setMessageReceiver(MessageReceiver r) {

        receiver = r;
    }

    @Override
    public void run() {
        ChatResponse response;
        try {
            while (connector.socketAvailable()) {
                response = new ChatResponse(reader.readLine());
                if (response == null) {
                    logger.info("Terminating ChatMessageReceiver: message received is null");
                    break;
                }
                if (receiver != null) {
                    receiver.messageArrived(response);
                }
            }
        } catch (IOException e) {
            logger.warning("readLine Error: " + e.getMessage());
        } finally {
            connector.invalidateSocket();
        }
    }

    @Override
    public void socketClosed() {

    }

    @Override
    public void socketConnected(Socket s) throws IOException {
        reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        new Thread(this).start();
    }
}
