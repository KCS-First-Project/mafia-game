package com.mafiachat.client.user;

public class ChatUser {
    String name;
    String id;
    String host;
    Boolean alive;

    private ChatUser(String id, String name, String host) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.alive = true;
    }

    public static ChatUser createChatUserAlive(String id, String name, String host) {
        return new ChatUser(id, name, host);
    }

    private ChatUser(String id, String name, String host, Boolean alive) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.alive = alive;
    }

    public static ChatUser createChatUserSetAlive(String id, String name, String host, Boolean alive) {
        return new ChatUser(id, name, host, alive);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
