package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {
    private final LokaLite plugin;

    public PlayerChatListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        //Remove arena player from pool so they can be properly recreated next time they join.
        Player player = e.getPlayer();
        e.setCancelled(true);

        plugin.chat.sendMessage(player.getName(), "public", e.getMessage(), true);
    }
}
