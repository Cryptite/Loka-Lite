package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.utils.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.cryptite.lite.utils.SoundUtil.playCustomSound;
import static org.bukkit.ChatColor.*;

public class PlayerInteractListener implements Listener {

    private final LokaLite plugin;

    public PlayerInteractListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        //Skydiving Achievement
        if (e.getAction().equals(Action.PHYSICAL)
                && e.getClickedBlock().getType().equals(Material.STONE_PLATE)) {

            Location plateLocation = e.getClickedBlock().getLocation();
            Player p = e.getPlayer();

            if (plateLocation.equals(plugin.sanyaPlate)) {
                p.teleport(plugin.sanya);

                sendWorldMessage(p, "Sanya", "First");
            } else if (plateLocation.equals(plugin.daPlate)) {
                p.teleport(plugin.da);

                sendWorldMessage(p, "Da", "Third");
            } else if (plateLocation.equals(plugin.akPlate)) {
                p.teleport(plugin.ak);

                sendWorldMessage(p, "Ak", "Second");
            }
        } else {
            e.setCancelled(true);
        }
    }

    private void sendWorldMessage(Player p, String world, String number) {
        plugin.scheduler.runTaskLater(plugin, () -> {
            new Title(GREEN + world, GRAY + "The " + number + " World").send(p);
            playCustomSound(p, "ZoneBell", .7f);
            p.sendMessage(GRAY + "Welcome to " + GREEN + world + GRAY + ", the " + number
                    + " world. You can return to the main hub at " + "any time by typing " + YELLOW + BOLD + "/hub");
        }, 20);
    }
}
