package network.server;

import static network.View.server.GroupManagerResponseView.countActiveClients;
import static network.View.server.GroupManagerResponseView.createMessage;
import static network.constant.ChatCommandUtil.ENTER_ROOM;
import static network.constant.ChatCommandUtil.EXIT_ROOM;
import static network.constant.ChatCommandUtil.NORMAL;
import static network.constant.ChatCommandUtil.USER_LIST;
import static network.constant.ChatCommandUtil.WHISPER;

import java.util.Vector;

public class GroupManager {
    private static final String LEFT_SERVER = " 님이 방을 나갔습니다.";
    private static final String ENTER_SERVER = " 님이 방에 입장했습니다.";

    private static Vector<MessageHandler> clientGroup = new Vector<>();

    public static void addMessageHandler(MessageHandler handler) {
        //broadcastMessage(handler.getId() + " has just entered chat room");
        clientGroup.add(handler);
        countActiveClients(clientGroup.size());
    }

    public static void removeMessageHandler(MessageHandler handler) {
        clientGroup.remove(handler);
        countActiveClients(clientGroup.size());
        for (MessageHandler sendMessage : clientGroup) {
            sendMessage.sendMessage(createMessage(EXIT_ROOM.getCommand(), handler.getName() + LEFT_SERVER));
        }
    }

    public static void broadcastMessage(String message) {
        for (MessageHandler handler : clientGroup) {
            handler.sendMessage(createMessage(NORMAL.getCommand(), message));
        }
    }

    public static void closeAllMessageHandlers() {
        for (MessageHandler handler : clientGroup) {
            handler.close();
        }
        clientGroup.clear();
    }

    public static void broadcastNewChatter(MessageHandler newHandler) {
        broadcastEnterRoomMessage(newHandler);
        broadcastUserListToAllClients(generateUserList());
    }

    private static String generateUserList() {
        StringBuilder users = new StringBuilder();
        for (int i = 0; i < clientGroup.size(); i++) {
            MessageHandler handler = clientGroup.get(i);
            users.append(handler.getName())
                    .append(",")
                    .append(handler.getId())
                    .append(",")
                    .append(handler.getFrom());
            if (i < (clientGroup.size() - 1)) {
                users.append("|");
            }
        }
        return users.toString();
    }

    private static void broadcastEnterRoomMessage(MessageHandler newHandler) {
        for (MessageHandler handler : clientGroup) {
            if (handler != newHandler) {
                handler.sendMessage(createMessage(ENTER_ROOM.getCommand(),
                        newHandler.getName() + ENTER_SERVER));
            }
        }
    }

    private static void broadcastUserListToAllClients(String userList) {
        for (MessageHandler handler : clientGroup) {
            handler.sendMessage(createMessage(USER_LIST.getCommand(), userList));
        }
    }


    public static void sendWhisper(MessageHandler from, String to, String msg) {
        msg = createMessage(WHISPER.getCommand(), msg);
        for (MessageHandler handler : clientGroup) {
            if (handler.getId().equals(to)) {
                handler.sendMessage(msg);
                break;
            }
        }
        from.sendMessage(msg);
    }
}