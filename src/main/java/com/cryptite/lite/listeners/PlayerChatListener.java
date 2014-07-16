package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.PvPPlayer;
import com.cryptite.lite.bungee.Chat;
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
        PvPPlayer p = plugin.getAccount(player.getName());

        e.setCancelled(true);

        Chat chat = new Chat(player.getName(), p.rank, e.getMessage(), p.townOwner, false, null, null);

        //Send to everyone
        plugin.globalChatMessage(plugin.parseChatMessage(chat), false);
        plugin.sendChatToNetwork(player.getName(), e.getMessage());
    }
}
