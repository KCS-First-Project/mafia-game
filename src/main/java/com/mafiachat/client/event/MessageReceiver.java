package com.mafiachat.client.event;

import com.mafiachat.protocol.ChatRequest;
import com.mafiachat.protocol.ChatResponse;

public interface MessageReceiver {
    public void messageArrived(ChatResponse response);
}
