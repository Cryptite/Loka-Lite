package com.cryptite.lite.network.events;

import java.util.EventListener;

public interface SocketHandlerReadyEventListener extends EventListener {
    public void socketHandlerReady(SocketHandlerReadyEvent evt);
}
