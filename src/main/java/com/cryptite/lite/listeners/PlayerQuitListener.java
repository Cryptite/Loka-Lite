package com.cryptite.lite.listeners;

import com.cryptite.lite.Account;
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
        //If the player is not in arenaplayers, their arena game is done and they'll be handled by Respawn most likely..
        if (!plugin.players.containsKey(event.getPlayer().getName())) return;

        Account p = plugin.players.get(event.getPlayer().getName());

        if (plugin.playersToReturn.contains(p.name)) {
            //Player disconnected of own accord and will thus return to Loka. Can safely remove.
            plugin.playersToReturn.remove(p.name);
        }

        //Remove arena player from pool so they can be properly recreated next time they join.
        plugin.players.remove(event.getPlayer().getName());

        event.setQuitMessage(YELLOW + event.getPlayer().getName() + GRAY + " has left the ether.");
    }
}
