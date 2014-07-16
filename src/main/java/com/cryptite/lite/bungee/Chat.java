package com.cryptite.lite.bungee;


@SuppressWarnings("FieldCanBeLocal")
public class Chat {

    public final String name;
    public final String rank;
    private final long timestamp;
    public final String message;
    public final Boolean townOwner;
    public final Boolean op;
    public final String town;
    public final String townTag;

    public Chat(String name, String rank, String message, Boolean townOwner, Boolean op, String town, String townTag) {
        this.name = name;
        this.rank = rank;
        this.message = message;
        this.townOwner = townOwner;
        this.op = op;
        this.timestamp = System.currentTimeMillis();
        this.town = town;
        this.townTag = townTag;
    }
}
