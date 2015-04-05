package com.cryptite.lite.network;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.Chat;
import com.google.gson.Gson;

public class SocketClient implements Runnable {
    private Client client;
    private final LokaLite plugin;
    private boolean socketConnected = false;

    public SocketClient(LokaLite plugin) {
        this.plugin = plugin;
        connect();
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, this, 20, 60);
    }

    private void connect() {
        client = new Client("play.lokamc.com", 9876);
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
        if (message.equals("pong")) return;

        String channel = message.split("~")[0];
        String msg = message.split("~")[1];

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
        if (!socketConnected) {
            connect();
            return;
        }

        client.SendMessage(destination + "~" + channel + "~" + data);
    }

    @Override
    public void run() {
        //If not connected, try to connect
        if (!socketConnected) {
            connect();
            return;
        }

        client.SendMessage("ping");
    }
}
