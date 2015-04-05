package com.cryptite.lite.network.events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class MessageReceivedEvent extends EventObject {

    private String message;

    private int id;

    public MessageReceivedEvent(Object source, int id, String message) {

        super(source);

        this.message = message;

    }

    public int getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

}
