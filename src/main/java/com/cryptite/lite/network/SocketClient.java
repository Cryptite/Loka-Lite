package com.cryptite.lite.network;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.Chat;
import com.google.gson.Gson;

import static com.cryptite.lite.utils.TimeUtil.secondsSince;

public class SocketClient implements Runnable {
    private Client client;
    private final LokaLite plugin;
    private boolean socketConnected = false;
    private long lastPong;

    public SocketClient(LokaLite plugin) {
        this.plugin = plugin;
        connect();
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, this, 20, 60);
    }

    private void connect() {
        if (client != null) {
            client.Disconnect();
            client = null;
            lastPong = 0;
        }

        client = new Client("pvp.lokamc.com", 9876);
        client.getHandler().getConnected().addSocketConnectedEventListener(evt -> {
            System.out.println("[Network] Connected to server!");
            socketConnected = true;
            client.SendMessage("~connect~" + plugin.serverName);
        });

        client.getHandler().getMessage().addMessageReceivedEventListener(evt -> parseMessage(evt.getMessage()));

        client.getHandler().getDisconnected().addSocketDisconnectedEventListener(evt -> {
            System.out.println("[Network] Disconnected");
            socketConnected = false;
        });
        client.Connect();
    }

    public void disconnect() {
        client.Disconnect();
    }

    private void parseMessage(String message) {
        if (message.equals("pong")) return;

        String channel = message.split("~")[0];
        String msg = message.split("~")[1];

        switch (channel) {
            case "Chat":
                //No need to do anything about chat if we're empty
                if (plugin.server.getOnlinePlayers().size() == 0) return;

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

        plugin.scheduler.runTask(plugin, () -> client.SendMessage(destination + "~" + channel + "~" + data));
    }

    @Override
    public void run() {
        //If not connected, try to connect
        if (!socketConnected || (lastPong > 0 && secondsSince(lastPong) > 20)) {
            socketConnected = false;
            connect();
            return;
        }

        int secondsSinceLastPong = secondsSince(lastPong);
        if (secondsSinceLastPong > 5) System.out.println("[Network] Last Pong: " + secondsSince(lastPong));

        client.SendMessage("ping");
    }
}
