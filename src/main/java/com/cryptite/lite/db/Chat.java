package com.cryptite.lite.db;


@SuppressWarnings("FieldCanBeLocal")
public class Chat {

    public final String server;
    public final String name;
    public final String channel;
    public final String message;

    public Chat(String server, String name, String channel, String message) {
        this.server = server;
        this.name = name;
        this.channel = channel;
        this.message = message;
    }
}
