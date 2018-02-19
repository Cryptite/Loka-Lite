package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final LokaLite plugin;

    public PlayerInteractListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        e.setCancelled(true);
    }
}
