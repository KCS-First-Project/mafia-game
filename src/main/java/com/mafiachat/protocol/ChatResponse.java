package com.mafiachat.protocol;

public class ChatResponse extends ChatData {

    public ChatResponse(String formattedMessage) {
        super(formattedMessage);
    }


    public static ChatResponse createResponse(Command command, String body) {
        String formattedMessage = createFormattedMessage(command, body);
        return new ChatResponse(formattedMessage);
    }
    public static ChatResponse createNormalResponse(String body) {
        return createResponse(Command.NORMAL, body);
    }

    public static ChatResponse createAliasInitResponse(ResultType resultType) {
        return createResponse(Command.INIT_ALIAS, resultType.name());
    }
}
