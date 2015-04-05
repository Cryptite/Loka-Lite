package com.cryptite.lite.network.events;

import java.util.EventListener;

public interface MessageReceivedEventListener extends EventListener {
    public void messageReceived(MessageReceivedEvent evt);
}
