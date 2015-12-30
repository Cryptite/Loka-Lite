package com.cryptite.lite.network;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.Chat;
import com.cryptite.lite.utils.LocationUtils;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.cryptite.lite.utils.LocationUtils.coordsToStringBasic;
import static com.cryptite.lite.utils.LocationUtils.parseCoord;
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

        client = new Client(plugin.config.get("networkserver", "pvp.lokamc.com"), 9876);
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
            case "regen":
                Location l1 = parseCoord(plugin, msg.split("-")[0]);
                Location l2 = parseCoord(plugin, msg.split("-")[1]);
                getRegenBlocks(l1, l2);
                break;
            default:
                System.out.println("[Network] Received unknown data on " + channel + ": " + msg);
                break;
        }
    }

    private void getRegenBlocks(Location l1, Location l2) {
        plugin.scheduler.runTask(plugin, () -> {
            System.out.println("[Regeneration] Received regen call from Loka");
            List<String> blocks = new ArrayList<>();
            for (Block b : LocationUtils.getBlocksFromRegion(l1, l2)) {
                blocks.add(coordsToStringBasic(b.getLocation()) + "," + b.getTypeId() + "," + b.getData());
            }
            System.out.println("[Regeneration] Loaded " + blocks.size() + " worth of data...");

            float totalBlocks = blocks.size();
            plugin.scheduler.runTaskAsynchronously(plugin, () -> {
                DBCollection collection = plugin.db.getCollection("regen");

                final long[] pushed = {0};
                while (blocks.size() > 0) {
                    List<String> blockUpdate = new ArrayList<>();
                    for (String block : new ArrayList<>(blocks)) {
                        blockUpdate.add(block);
                        if (blockUpdate.size() > 2499) break;
                    }
                    String id = UUID.randomUUID().toString();
                    plugin.scheduler.runTaskLaterAsynchronously(plugin, () -> {
                        collection.insert(new BasicDBObject("blocks_" + id, blockUpdate));
                        pushed[0] += blockUpdate.size();
                        int percent = (int) (((float) pushed[0] / totalBlocks) * 100);
                        System.out.println("[Regeneration] Pushed " + percent + "% db (" + blocks.size() + " left)");
                        sendMessage("loka", "blocks_" + percent + "_" + blocks.size() + "_" + id, "regenblocks");
                    }, 40);
                    blocks.removeAll(blockUpdate);
                }

                sendMessage("loka", "done", "regenblocks");
            });
        });
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
