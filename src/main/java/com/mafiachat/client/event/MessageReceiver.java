package com.mafiachat.client.event;

import com.mafiachat.protocol.ChatRequest;

public interface MessageReceiver {
    public void messageArrived(ChatRequest request);
}
