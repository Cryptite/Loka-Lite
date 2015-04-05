package com.cryptite.lite.network.events;

import com.cryptite.lite.network.SocketHandler;

import java.util.EventObject;

@SuppressWarnings("serial")
public class SocketHandlerReadyEvent extends EventObject {

    private SocketHandler handler;

    public SocketHandlerReadyEvent(Object source, SocketHandler handler) {

        super(source);

        this.handler = handler;

    }

    public SocketHandler getHandler() {
        return handler;
    }

}
