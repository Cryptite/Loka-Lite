package com.cryptite.lite.modules;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.utils.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.cryptite.lite.utils.SoundUtil.playCustomSound;
import static org.bukkit.ChatColor.*;

public class OldWorlds implements Listener {
    private final LokaLite plugin;

    public OldWorlds(LokaLite plugin) {
        this.plugin = plugin;
    }

    public void sendWorldMessage(Player p, String[] info) {
        sendWorldMessage(p, info[0], info[1]);
    }

    public void sendWorldMessage(Player p, String world, String number) {
        if (world == null) return;

        plugin.scheduler.runTaskLater(plugin, () -> {
            new Title(GREEN + world, GRAY + "The " + number + " World").send(p);
            playCustomSound(p, "ZoneBell", .7f);
            p.sendMessage(GRAY + "Welcome to " + GREEN + world + GRAY + ", the " + number
                    + " world. You may " + GREEN + "fly" + GRAY + " here.");
            p.sendMessage(GRAY + "You can return to the main hub at " + "any time by typing " + YELLOW + BOLD + "/hub");
        }, 20);
    }

    @EventHandler(priority = EventPriority.HIGH)
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
//            e.setCancelled(true);
        }
    }

    public String[] getWorldNames(String world) {
        String[] info = new String[2];
        switch (world) {
            case "world_artifact":
                info[0] = "Sanya";
                info[1] = "First";
                return info;
            case "world_blight":
                info[0] = "Ak";
                info[1] = "Second";
                return info;
            case "world3":
                info[0] = "Da";
                info[1] = "Third";
                return info;
        }

        return info;
    }
}
