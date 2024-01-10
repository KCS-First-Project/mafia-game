package com.mafiachat.protocol;

public class ChatRequest extends ChatData {

    public ChatRequest(String formattedMessage) {
        super(formattedMessage);
    }

    public static ChatRequest createRequest(Command command, String body) {
        String formattedMessage = createFormattedMessage(command, body);
        return new ChatRequest(formattedMessage);
    }

    public static ChatRequest createAliasInitRequest(String name) {
        return createRequest(Command.INIT_ALIAS, name);
    }

    public static ChatRequest createNormalRequest(String chatName, String body) {
        return createRequest(Command.NORMAL, "%s: %s".formatted(chatName, body));
    }

    public static ChatRequest createSystemRequest(String body) {
        return createRequest(Command.SYSTEM, "[SYSTEM] " + body);
    }
}
