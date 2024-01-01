package network.constant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

//    public static String command(String command) {
//        return Arrays.stream(ChatCommandUtil.values())
//                .filter(commandUtil -> commandUtil.command.equals(command))
//                .findAny()
//                .orElse(NOMATCH)
//                .getCommand();
//    }


    public static String parseCommand(String msg) {
        Matcher matcher = Pattern.compile("\\[([a-zA-Z]+)\\]").matcher(msg);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return ChatCommandUtil.UNKNOWN.getCommand();
        }
    }

    public static String parseMessage(String msg) {
        return msg.replaceFirst("\\[([a-zA-Z]+)\\]", "");
    }

    public static String formattingMessage(String message) {
        int closeBracketIndex = message.indexOf(CLOSE_BRACKET);
        if (message.startsWith(String.valueOf(OPEN_BRACKET)) && closeBracketIndex > 0) {
            return message.substring(closeBracketIndex + 1);
        }
        return message;
    }
}
