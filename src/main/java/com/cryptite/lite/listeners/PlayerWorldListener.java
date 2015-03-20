package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerWorldListener implements Listener {
    private final LokaLite plugin;

    public PlayerWorldListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent e) {
        //Remove arena player from pool so they can be properly recreated next time they join.
        if (!e.getTo().getWorld().equals(plugin.world)) {
            e.getPlayer().setAllowFlight(true);
        }
    }
}
