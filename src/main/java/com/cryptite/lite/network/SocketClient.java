package com.cryptite.lite.network;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.Chat;
import com.google.gson.Gson;

public class SocketClient {
    private final Client client = new Client("play.lokamc.com", 9876);
    private final LokaLite plugin;
    private boolean socketConnected = false;

    public SocketClient(LokaLite plugin) {
        this.plugin = plugin;

        client.getHandler().getConnected().addSocketConnectedEventListener(evt -> {
            System.out.println("Client - Connected to server!");
            socketConnected = true;
        });

        client.getHandler().getMessage().addMessageReceivedEventListener(evt -> parseMessage(evt.getMessage()));

        client.getHandler().getDisconnected().addSocketDisconnectedEventListener(evt -> {
            System.out.println("Client - Disconnected");
            socketConnected = false;
        });
        client.Connect();
    }

    private void parseMessage(String message) {
        String channel = message.split("~")[1];
        String msg = message.split("~")[2];

        switch (channel) {
            case "Chat":
                parseChatMessage(msg);
                break;
            default:
                System.out.println("[Network] Received unknown data on " + channel + ": " + msg);
                break;
        }
    }

    void parseChatMessage(String msg) {
        Chat chat = new Gson().fromJson(msg, Chat.class);
        plugin.chat.sendMessage(chat, false);
    }

    public void sendMessage(String destination, String data, String channel) {
        if (!socketConnected)
            client.Connect();

        client.SendMessage(destination + "~" + channel + "~" + data);
    }
}
