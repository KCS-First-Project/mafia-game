package network.constant;

public enum ChatCommandUtil {
    NOMATCH("noMatch"),
    WHISPER("whisper"),
    NORMAL("normal"),
    INIT_ALIAS("initAlias"),
    USER_LIST("userList"),
    ENTER_ROOM("enterRoom"),
    EXIT_ROOM("exitRoom"),
    UNKNOWN("unknown");

    private String command;

    ChatCommandUtil(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    private static final char OPEN_BRACKET = '[';
    private static final char CLOSE_BRACKET = ']';

//    public static String getCommand(String command) {
//        return Arrays.stream(ChatCommandUtil.values())
//                .filter(commandUtil -> commandUtil.command.equals(command))
//                .findAny()
//                .orElse(NOMATCH)
//                .getCommand();
//    }

    public static String getCommand(String msg) {
        String[] parts = msg.split("\\[|\\]");
        if (parts.length > 1 && parts[0].isEmpty() && parts[1].length() == 1 && parts[1].matches("[a-z]")) {
            return parts[1];
        } else {
            return UNKNOWN.getCommand();
        }
    }

    public static String formattingMessage(String message) {
        int closeBracketIndex = message.indexOf(CLOSE_BRACKET);
        if (message.startsWith(String.valueOf(OPEN_BRACKET)) && closeBracketIndex > 0) {
            return message.substring(closeBracketIndex + 1);
        }
        return message;
    }
}
