package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.db.OldWorld;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashMap;
import java.util.Map;

import static com.lokamc.LokaCore.network;

public class PlayerJoinListener implements Listener {
    private final LokaLite plugin;
    private final Map<String, String> destinations = new HashMap<>();

    public PlayerJoinListener(LokaLite plugin) {
        this.plugin = plugin;
        network.subscribe("oldworld", OldWorld.class, this::receiveDestination);
    }

    private void receiveDestination(OldWorld world) {
        destinations.put(world.name, world.world);
        System.out.println(world.name + " should go to " + world.world);
    }

    @EventHandler
    public void onPlayerPreJoin(PlayerSpawnLocationEvent e) {
        Player p = e.getPlayer();
        if (plugin.oldWorlds != null) {
            String destinaton = destinations.getOrDefault(p.getName(), null);
            if (destinaton != null) {
                switch (destinations.get(p.getName())) {
                    case "world1":
                        if (!p.getWorld().equals(plugin.sanya.getWorld())) {
                            e.setSpawnLocation(plugin.sanya);
                        }
                        break;
                    case "world2":
                        if (!p.getWorld().equals(plugin.ak.getWorld())) {
                            e.setSpawnLocation(plugin.ak);
                        }
                        break;
                    case "world3":
                        if (!p.getWorld().equals(plugin.da.getWorld())) {
                            e.setSpawnLocation(plugin.da);
                        }
                        break;
                    case "world4":
                        if (!p.getWorld().equals(plugin.taan.getWorld())) {
                            e.setSpawnLocation(plugin.taan);
                        }
                        break;
                }
                plugin.scheduler.runTaskLater(plugin, () -> plugin.oldWorlds.sendPlayer(p, destinations.get(p.getName())), 5);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);

        player.setAllowFlight(!player.getWorld().equals(plugin.spawn.getWorld()));

        String joinMsg = ChatColor.translateAlternateColorCodes('&', plugin.config.get("joinmessage", ""));
        event.setJoinMessage(joinMsg.replace("<player>", event.getPlayer().getName()));

        String msg = ChatColor.translateAlternateColorCodes('&', plugin.config.get("welcomemessage", ""));
        player.sendMessage(msg);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        e.getPlayer().setAllowFlight(true);
    }
}
