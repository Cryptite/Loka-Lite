package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockListener implements Listener {

    private final LokaLite plugin;

    public BlockListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFade(BlockFadeEvent e) {
        Material type = e.getBlock().getType();
        if (type.toString().contains("CORAL")) {
            e.setCancelled(true);
        }
    }
}
