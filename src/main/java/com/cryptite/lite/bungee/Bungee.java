package com.cryptite.lite.bungee;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.Chat;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.ChatColor.*;

public class Bungee implements PluginMessageListener, Listener {
    private final Logger log = Logger.getLogger("LokaPvP-Bungee");
    private final LokaLite plugin;
    private static String serverName; // Example: using the GetServer subchannel

    private final int bungeeServerTimeout = 12;
    private final List<String> playersInTransit = new ArrayList<>();

    //Grace period is when the the first player joins after the server was empty before bungee will spit out chat
    //from other players. This is to prevent a huge spam of chat buildup when the server's been empty for awhile.
    public boolean muteChatFromLoka = true;

    public Bungee(LokaLite plugin) {
        this.plugin = plugin;
        serverName = "oldworld";
        plugin.server.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.server.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    public void sendPlayer(final Player p) {
        if (!playersInTransit.contains(p.getName())) {
            playersInTransit.add(p.getName());

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("loka");

            log.info("Sending " + p.getName() + " back to Loka");
            p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

            //Players may fail to switch servers after the timeout length, so, 2 seconds after the timeout would occur,
            //try to send them again automatically.
            plugin.scheduler.runTaskLater(plugin, () -> {
                if (p.isOnline()) {
                    checkPlayerWasSent(p.getName());
                }
            }, 20 * (bungeeServerTimeout + 2));
        }
    }

    void checkPlayerWasSent(String player) {
        if (playersInTransit.contains(player)) {
            Player p = plugin.server.getPlayerExact(player);
            if (p != null) {
                log.info("Trying to send " + p.getName() + " again.");
                sendPlayer(p);
            }
        }
    }

    public void sendMessage(String bungeeChannel, String message, String channel) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        byte[] data = message.getBytes();
        try {
            out.writeUTF("Forward");
            out.writeUTF(bungeeChannel); // Target Server

            /* A "subchannel" much like Forward, Connect, etc. Think of it as a way of identifying what plugin sent the message to Bungee, I guess? It's mainly for plugin communication between servers I'd say */
            out.writeUTF(channel);
            out.writeShort(data.length); // The length of the rest of the data.
            out.write(data); // Write out the rest of the data.
        } catch (IOException e) {
            // Can never happen
        }

        if (plugin.server.getOnlinePlayers().size() > 0) {
            plugin.server.getOnlinePlayers().iterator().next().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        System.out.println(s);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String channelIn = in.readUTF();
            String msg = in.readUTF();
            if (channelIn.equalsIgnoreCase("Chat")) {
                parseChatMessage(msg);
            } else if (channelIn.equals("PlayerCount")) {
                sendPlayerList();
            } else if (channelIn.equals("Achievement")) {
                String playerName = msg.split("\\.")[0];
                Achievement a = new Gson().fromJson(msg.split("\\.")[1], Achievement.class);
                unlockAchievement(playerName, a);
            } else {
                log.info("[Bungee-" + channelIn + "] - " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendPlayerList() {
        StringBuilder b = new StringBuilder();
        for (Player p : plugin.server.getOnlinePlayers()) {
            b.append(p.getName()).append(",");
        }
        sendMessage("loka", b.toString(), "PlayerCount");
    }

    void parseChatMessage(String msg) {
        //No chat allowed during the grace period.
        if (muteChatFromLoka) return;

        Chat chat = new Gson().fromJson(msg, Chat.class);
        plugin.chat.sendMessage(chat, false);
    }

    void unlockAchievement(String name, Achievement achievement) {
        Player p = plugin.server.getPlayerExact(name);

        if (p != null) {
            //Play level up sound
            p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
        }

        //Announce achievement
        for (Player player : plugin.server.getOnlinePlayers()) {
            switch (achievement.type) {
                case "common":
                    new FancyMessage(name + " has just earned the achievement ")
                            .color(WHITE)
                            .then("[" + achievement.title + "]")
                            .color(GREEN)
                            .tooltip(achievement.getAchievementText())
//                            .tooltip(GREEN + achievement.title + "\n" + ITALIC + DARK_GRAY + "Common Loka Achievement\n" +
//                                    WHITE + achievement.getAchievementText())
                            .send(player);
                    break;
                case "rare":
                    new FancyMessage(name + " has earned the " + DARK_AQUA + "rare achievement ")
                            .color(WHITE)
                            .then("[" + achievement.title + "]")
                            .color(YELLOW)
                            .tooltip(achievement.getAchievementText())
//                            .tooltip(YELLOW + achievement.title + "\n" + ITALIC + GRAY + "Rare Loka Achievement\n" +
//                                    WHITE + achievement.getAchievementText())
                            .send(player);
                    break;
                case "legendary":
                    new FancyMessage(name + " has earned the " + YELLOW + "legendary achievement ")
                            .color(WHITE)
                            .then("[" + achievement.title + "]")
                            .color(GOLD)
                            .tooltip(achievement.getAchievementText())
//                            .tooltip(GOLD + achievement.title + "\n" + ITALIC + YELLOW + "Legendary Loka Achievement\n" +
//                                    WHITE + achievement.getAchievementText())
                            .send(player);
                    break;
            }
        }

        log.info(name + " earned the achievement: " + achievement.title);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        //Successful quit, so remove from bungee tracking
        String player = event.getPlayer().getName();
        if (playersInTransit.contains(player)) {
            playersInTransit.remove(player);
        }
    }
}
