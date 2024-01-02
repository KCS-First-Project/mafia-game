//package com.mafiachat.client;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.Socket;
//import com.mafiachat.View.client.ChatMessageReceiverResponseView;
//import com.mafiachat.client.event.ChatSocketListener;
//import com.mafiachat.client.event.ConnectionManager;
//import com.mafiachat.client.event.MessageReceiver;
//import com.mafiachat.exception.NoResourceException;
//
//public class ChatMessageReceiver implements Runnable, ChatSocketListener {
//    private static final String EMPTY_RECEIVE = "Received null, terminating ChatMessageReceiver.";
//    private BufferedReader reader;
//    private MessageReceiver receiver;
//    private final ConnectionManager connectionManager;
//
//    private ChatMessageReceiver(ConnectionManager connectionManager) {
//        this.connectionManager = connectionManager;
//    }
//
//    public static ChatMessageReceiver createChatMessageReceiver(ConnectionManager connectionManager) {
//        return new ChatMessageReceiver(connectionManager);
//    }
//
//    public void setMessageReceiver(MessageReceiver messageReceiver) {
//        receiver = messageReceiver;
//    }
//
//    public void run() {
//        try {
//            String msg;
//            while (connectionManager.socketAvailable()) {
//                msg = reader.readLine();
//                if (msg == null) {
//                    throw NoResourceException.errorMessage(EMPTY_RECEIVE);
//                }
//                if (receiver != null) {
//                    receiver.messageArrived(msg);
//                }
//            }
//        } catch (IOException e) {
//            ChatMessageReceiverResponseView.failToReceiveMessage(e.getMessage());
//        } finally {
//            connectionManager.invalidateSocket();
//        }
//    }
//
//
//    public void socketClosed() {
//    }
//
//    public void socketConnected(Socket s) throws IOException {
//        reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//        new Thread(this).start();
//    }
//}
