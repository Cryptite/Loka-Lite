package com.cryptite.lite.network.events;

import com.cryptite.lite.network.SocketHandler;

import java.util.EventObject;

@SuppressWarnings("serial")
public class SocketConnectedEvent extends EventObject {

    private int id;

    private SocketHandler handler;

    public SocketConnectedEvent(Object source, SocketHandler handler, int id) {

        super(source);

        this.id = id;
        this.handler = handler;

    }

    public SocketHandler getHandler() {
        return handler;
    }

    public int getID() {
        return id;
    }

}
