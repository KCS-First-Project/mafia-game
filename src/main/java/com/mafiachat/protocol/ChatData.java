package com.mafiachat.protocol;

public class ChatData {
    protected final String formattedMessage;
    protected final Command command;
    protected final String body;

    public ChatData(String formattedMessage) {
        this.command = extractCommand(formattedMessage);
        this.body = extractRequestBody(formattedMessage);
        this.formattedMessage = formattedMessage;
    }

    private ChatData(Command command, String body) {
        this.command = command;
        this.body = body;
        this.formattedMessage = createFormattedMessage(command, body);
    }

    public Command getCommand() {
        return this.command;
    }

    public String getBody() {
        return this.body;
    }

    public String getFormattedMessage() {
        return this.formattedMessage;
    }

    protected static String createFormattedMessage(Command command, String body) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.delete(0, msgBuilder.length());
        msgBuilder.append("[");
        msgBuilder.append(command.name());
        msgBuilder.append("]");
        msgBuilder.append(body);
        return msgBuilder.toString();
    }

    protected Command extractCommand(String request) {
        try {
            String command = request.split("[\\[\\]]")[1];
            return Command.valueOf(command);
        } catch (Exception e) {
            return Command.UNKNOWN;
        }
    }

    protected String extractRequestBody(String request) {
        return request.replaceFirst("\\[\\w+\\]", "");
    }
}
