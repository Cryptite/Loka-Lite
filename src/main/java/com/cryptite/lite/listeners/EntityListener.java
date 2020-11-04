package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityListener implements Listener {

    private final LokaLite plugin;

    public EntityListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(PlayerNaturallySpawnCreaturesEvent e) {
        e.setCancelled(true);
    }
}
