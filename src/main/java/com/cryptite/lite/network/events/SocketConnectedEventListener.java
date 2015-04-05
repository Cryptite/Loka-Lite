package com.cryptite.lite.network.events;

import java.util.EventListener;

public interface SocketConnectedEventListener extends EventListener {
    public void socketConnected(SocketConnectedEvent evt);
}
