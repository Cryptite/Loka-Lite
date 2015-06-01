package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {
    private final Logger log = Logger.getLogger("Artifact-Join");
    private final LokaLite plugin;

    public PlayerJoinListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);

        if (!player.getWorld().equals(plugin.spawn)) {
            if (plugin.oldWorlds != null)
                plugin.oldWorlds.sendWorldMessage(player, plugin.oldWorlds.getWorldNames(player.getWorld().getName().toLowerCase()));
        }

        player.setAllowFlight(!player.getWorld().equals(plugin.spawn));

        if (plugin.oldWorlds != null &&
                (player.getWorld().getName().equals("world")
                        || !player.hasPlayedBefore()))
            plugin.scheduler.runTaskLater(plugin, () -> player.teleport(plugin.spawn), 20);

        String joinMsg = ChatColor.translateAlternateColorCodes('&', plugin.config.get("joinmessage", ""));
        event.setJoinMessage(joinMsg.replace("<player>", event.getPlayer().getName()));

        String msg = ChatColor.translateAlternateColorCodes('&', plugin.config.get("welcomemessage", ""));
        player.sendMessage(msg);

        plugin.getAccount(player.getName());
    }
}
