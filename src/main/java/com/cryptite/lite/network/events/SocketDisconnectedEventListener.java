package com.cryptite.lite.network.events;

import java.util.EventListener;

public interface SocketDisconnectedEventListener extends EventListener {
    public void socketDisconnected(SocketDisconnectedEvent evt);
}
