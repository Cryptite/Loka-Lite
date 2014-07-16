package com.cryptite.lite.bungee;


@SuppressWarnings("FieldCanBeLocal")
public class AllianceChat {

    public final String name;
    private final long timestamp;
    public final String town;
    public final String message;
    public final String alliance;

    public AllianceChat(String name, String town, String message, String alliance) {
        this.name = name;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.town = town;
        this.alliance = alliance;
    }
}
