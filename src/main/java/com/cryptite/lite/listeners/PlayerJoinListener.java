package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.OldWorld;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {
    private final Logger log = Logger.getLogger("Artifact-Join");
    private final LokaLite plugin;
    private Map<String, String> destinations = new HashMap<>();

    public PlayerJoinListener(LokaLite plugin) {
        this.plugin = plugin;
        plugin.mq.subscribe("oldworld", OldWorld.class, this::receiveDestination);
    }

    private void receiveDestination(OldWorld world) {
        destinations.put(world.name, world.world);
        System.out.println(world.name + " should go to " + world.world);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);

        if (plugin.oldWorlds != null) {
            String destinaton = destinations.getOrDefault(player.getName(), null);
            if (destinaton != null) {
                plugin.scheduler.runTaskLater(plugin, () -> plugin.oldWorlds.sendPlayer(player, destinations.get(player.getName())), 5);
            }
        }

        player.setAllowFlight(!player.getWorld().equals(plugin.spawn));

        String joinMsg = ChatColor.translateAlternateColorCodes('&', plugin.config.get("joinmessage", ""));
        event.setJoinMessage(joinMsg.replace("<player>", event.getPlayer().getName()));

        String msg = ChatColor.translateAlternateColorCodes('&', plugin.config.get("welcomemessage", ""));
        player.sendMessage(msg);

        plugin.getAccount(player.getName());
    }
}
