package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import com.cryptite.lite.utils.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.cryptite.lite.utils.SoundUtil.playCustomSound;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

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

            System.out.println("if " + plateLocation + " ========= " + plugin.sanyaPlate);
            System.out.println("if " + plateLocation + " ========= " + plugin.daPlate);
            System.out.println("if " + plateLocation + " ========= " + plugin.akPlate);

            if (plateLocation.equals(plugin.sanyaPlate)) {
                System.out.println("sanya");
                e.getPlayer().teleport(plugin.sanya);
                new Title(GREEN + "Sanya", GRAY + "The First World").send(e.getPlayer());
                playCustomSound(e.getPlayer(), "ZoneBell", .7f);
            } else if (plateLocation.equals(plugin.daPlate)) {
                System.out.println("da");
                e.getPlayer().teleport(plugin.da);
                new Title(GREEN + "Da", GRAY + "The Second World").send(e.getPlayer());
                playCustomSound(e.getPlayer(), "ZoneBell", .7f);
            } else if (plateLocation.equals(plugin.akPlate)) {
                System.out.println("ak");
                e.getPlayer().teleport(plugin.ak);
                new Title(GREEN + "Ak", GRAY + "The Third World").send(e.getPlayer());
                playCustomSound(e.getPlayer(), "ZoneBell", .7f);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
