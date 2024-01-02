package main.java.com.mafiachat.client.domain;

public class ChatUser {
    String name;
    String id;
    String host;

    public ChatUser(String name, String id, String host) {
        this.name = name;
        this.id = id;
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return name;
    }

    public String getHost() {
        return host;
    }
}
