package com.cryptite.lite.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(final EntityDamageEvent e) {
        e.setCancelled(true);
    }
}