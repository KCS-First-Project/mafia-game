package network.server;

import static network.View.server.GroupManagerResponseView.countActiveClients;
import static network.View.server.GroupManagerResponseView.createMessage;
import static network.constant.ChatCommandUtil.ENTER_ROOM;
import static network.constant.ChatCommandUtil.EXIT_ROOM;
import static network.constant.ChatCommandUtil.NORMAL;
import static network.constant.ChatCommandUtil.USER_LIST;
import static network.constant.ChatCommandUtil.WHISPER;

import network.server.domain.ClientGroup;

public class GroupManager {
    private static final String LEFT_SERVER = " 님이 방을 나갔습니다.";
    private static final String ENTER_SERVER = " 님이 방에 입장했습니다.";

    private ClientGroup clientGroup;

    private GroupManager() {
        clientGroup = ClientGroup.createClientGroup();
    }

    public static GroupManager createGroupManager() {
        return new GroupManager();
    }

    public ClientGroup getClientGroup() {
        return clientGroup;
    }

    public static void addMessageHandler(ClientGroup clientGroup, MessageHandler handler) {
        //broadcastMessage(handler.getId() + " has just entered chat room");
        clientGroup.addClient(handler);
        countActiveClients(clientGroup.getActiveClientCount());
    }

    public static void removeMessageHandler(ClientGroup clientGroup, MessageHandler handler) {
        clientGroup.removeClient(handler);
        countActiveClients(clientGroup.getActiveClientCount());
        String exitMessage = createMessage(EXIT_ROOM.getCommand(), handler.getName() + LEFT_SERVER);

        for (MessageHandler messageHandler : clientGroup.getClients()) {
            messageHandler.sendMessage(exitMessage);
        }
    }

    public static void broadcastMessage(ClientGroup clientGroup, String message) {
        for (MessageHandler handler : clientGroup.getClients()) {
            handler.sendMessage(createMessage(NORMAL.getCommand(), message));
        }
    }

    public static void closeAllMessageHandlers(ClientGroup clientGroup) {
        for (MessageHandler handler : clientGroup.getClients()) {
            handler.close();
        }
        clientGroup.clearClient();
    }

    public static void broadcastNewChatter(ClientGroup clientGroup, MessageHandler newHandler) {
        for (MessageHandler handler : clientGroup.getClients()) {
            if (handler != newHandler) {
                handler.sendMessage(createMessage(ENTER_ROOM.getCommand(), newHandler.getName() + ENTER_SERVER));
            }
            handler.sendMessage(createMessage(USER_LIST.getCommand(), generateUserList(clientGroup)));
        }
    }

    private static String generateUserList(ClientGroup clientGroup) {
        StringBuilder users = new StringBuilder();
        for (int i = 0; i < clientGroup.getActiveClientCount(); i++) {
            MessageHandler handler = clientGroup.getClients().get(i);
            users.append(handler.getName())
                    .append(",")
                    .append(handler.getId())
                    .append(",")
                    .append(handler.getFrom());
            if (i < (clientGroup.getActiveClientCount() - 1)) {
                users.append("|");
            }
        }
        return users.toString();
    }


    public static void sendWhisper(ClientGroup clientGroup, MessageHandler from, String to, String msg) {
        msg = createMessage(WHISPER.getCommand(), msg);
        for (MessageHandler handler : clientGroup.getClients()) {
            if (handler.getId().equals(to)) {
                handler.sendMessage(msg);
                break;
            }
        }
        from.sendMessage(msg);
    }
}
