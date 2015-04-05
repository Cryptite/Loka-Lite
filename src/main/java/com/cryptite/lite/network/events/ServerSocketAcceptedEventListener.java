package com.cryptite.lite.network.events;

import java.util.EventListener;

public interface ServerSocketAcceptedEventListener extends EventListener {
    public void socketAccepted(ServerSocketAcceptedEvent evt);
}
