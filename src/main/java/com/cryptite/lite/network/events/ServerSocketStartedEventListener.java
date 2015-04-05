package com.cryptite.lite.network.events;

import java.util.EventListener;

public interface ServerSocketStartedEventListener extends EventListener {
    public void serverSocketStarted(ServerSocketStartedEvent evt);
}
