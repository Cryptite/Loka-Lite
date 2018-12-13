package com.cryptite.lite.bungee;

import com.cryptite.lite.LokaLite;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Bungee implements Listener {
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        //Successful quit, so remove from bungee tracking
        String player = event.getPlayer().getName();
        if (playersInTransit.contains(player)) {
            playersInTransit.remove(player);
        }
    }
}
