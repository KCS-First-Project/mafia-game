package com.mafiachat.client.event;

import com.mafiachat.protocol.ChatResponse;

public interface MessageReceiver {
    void messageArrived(ChatResponse response);
}
