package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.YELLOW;

public class PlayerQuitListener implements Listener {
    private final Logger log = Logger.getLogger("LokaPvP-PlayerQuitListener");
    private final LokaLite plugin;

    public PlayerQuitListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.accounts.remove(event.getPlayer().getUniqueId());
        event.setQuitMessage(YELLOW + event.getPlayer().getName() + GRAY + " has left the ether.");
    }
}
