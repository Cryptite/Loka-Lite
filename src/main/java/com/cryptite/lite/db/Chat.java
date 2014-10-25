package com.cryptite.lite.db;


@SuppressWarnings("FieldCanBeLocal")
public class Chat {

    public final String name;
    public final String channel;
    private final long timestamp;
    public final String message;

    public Chat(String name, String channel, String message) {
        this.name = name;
        this.channel = channel;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
