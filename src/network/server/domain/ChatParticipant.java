package network.server.domain;

public class ChatParticipant {
    private String chatName;
    private String id;
    private String host;

    private ChatParticipant(String chatName, String id, String host) {
        this.chatName = chatName;
        this.id = id;
        this.host = host;
    }

    public static ChatParticipant createChatParticipant(String chatName, String id, String host) {
        return new ChatParticipant(chatName, id, host);
    }

    public String getId() {
        return id;//socket.getRemoteSocketAddress().toString();
    }

    public String getFrom() {
        return host;
    }

    public String getName() {
        return chatName;
    }

    public void changeNameAndId(String[] nameWithId) {
        this.chatName = nameWithId[0];
        this.id = nameWithId[1];
    }
}
