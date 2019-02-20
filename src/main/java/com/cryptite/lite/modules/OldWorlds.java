package com.cryptite.lite.modules;

import com.cryptite.lite.LokaLite;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static com.cryptite.lite.utils.SoundUtil.playCustomSound;
import static org.bukkit.ChatColor.*;

public class OldWorlds implements Listener {
    private final LokaLite plugin;

    public OldWorlds(LokaLite plugin) {
        this.plugin = plugin;
    }

    public void sendPlayer(Player p, String world) {
        switch (world) {
            case "world1":
                p.setGameMode(GameMode.ADVENTURE);
                p.setAllowFlight(true);
                sendWorldMessage(p, "Sanya", "First");
                break;
            case "world2":
                p.setGameMode(GameMode.ADVENTURE);
                p.setAllowFlight(true);
                sendWorldMessage(p, "Ak", "Second");
                break;
            case "world3":
                p.setGameMode(GameMode.ADVENTURE);
                p.setAllowFlight(true);
                sendWorldMessage(p, "Da", "Third");
                break;
            case "world4":
                p.setGameMode(GameMode.ADVENTURE);
                p.setAllowFlight(true);
                sendWorldMessage(p, "Taan", "Fourth");
                break;
        }
    }

    private void sendWorldMessage(Player p, String world, String number) {
        if (world == null) return;

        plugin.scheduler.runTaskLater(plugin, () -> {
            p.sendTitle(GREEN + world, GRAY + "The " + number + " World", 20, 80, 20);
            playCustomSound(p, "ZoneBell", .7f);
            p.sendMessage(GRAY + "Welcome to " + GREEN + world + GRAY + ", the " + number
                    + " world. You may " + GREEN + "fly" + GRAY + " here.");
            p.sendMessage(GRAY + "You can return to the Loka at any time by typing " + YELLOW + BOLD + "/leave");
        }, 20);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    public String[] getWorldNames(String world) {
        String[] info = new String[2];
        switch (world) {
            case "world_artifact":
                info[0] = "Sanya";
                info[1] = "First";
                return info;
            case "world_loka":
                info[0] = "Ak";
                info[1] = "Second";
                return info;
            case "world3":
                info[0] = "Da";
                info[1] = "Third";
                return info;
            case "fourthworld":
                info[0] = "Taan";
                info[1] = "Fourth";
                return info;
        }

        return info;
    }
}
