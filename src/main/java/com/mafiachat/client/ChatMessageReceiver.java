package com.mafiachat.client;

import com.mafiachat.client.event.ChatConnector;
import com.mafiachat.client.event.ChatSocketListener;
import com.mafiachat.client.event.MessageReceiver;
import com.mafiachat.protocol.ChatRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatMessageReceiver implements Runnable, ChatSocketListener {
    private BufferedReader reader;
    private MessageReceiver receiver;
    ChatConnector connector;

    public ChatMessageReceiver(ChatConnector c){
        connector = c;
    }

    public void setMessageReceiver(MessageReceiver r){

        receiver = r;
    }
    @Override
    public void run() {
        ChatRequest request;
        try{
            while(connector.socketAvailable()){
                request = new ChatRequest(reader.readLine());
                if(request == null){
                    System.out.println("Terminating ChatMessageReceiver: message received is null");
                    break;
                }
                if(receiver != null) receiver.messageArrived(request);
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
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
