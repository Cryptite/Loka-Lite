package com.cryptite.lite.bungee;


@SuppressWarnings("FieldCanBeLocal")
public class SimpleChat {

    private final String name;
    private final long timestamp;
    private final String message;
    private final Boolean town;
    private final Boolean alliance;

    public SimpleChat(String name, String message, Boolean town, Boolean alliance) {
        this.name = name;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.town = town;
        this.alliance = alliance;
    }
}
