package com.mafiachat.client;

public class ChatUser {
	String name;
	String id;
	String host;
	Boolean alive;
	public ChatUser(String id, String name, String host) {
		this.id = id;
		this.name = name;
		this.host = host;
		this.alive = true;
	}

	public ChatUser(String id, String name, String host,Boolean alive) {
		this.id = id;
		this.name = name;
		this.host = host;
		this.alive = alive;
	}
	public String getName() {
		return name;
	}
	public String getId() {
		return id;
	}
	public String toString () {
		return name;
	}
	public String getHost() {
		return host;
	}

	public Boolean getAlive() {return alive;}
}
