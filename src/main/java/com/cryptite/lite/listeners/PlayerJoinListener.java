package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mcsg.double0negative.tabapi.TabAPI;

import java.util.logging.Logger;

public class PlayerJoinListener implements Listener {
    private final Logger log = Logger.getLogger("Artifact-Join");
    private final LokaLite plugin;

    public PlayerJoinListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final String player = event.getPlayer().getName();

        //Wipe inventory and armor always
        event.getPlayer().getInventory().clear();
        event.getPlayer().getInventory().setArmorContents(null);

        //Wipe Tab list stuff
        TabAPI.setPriority(plugin, event.getPlayer(), -2);

        //Check for chat spam grace period
        checkGracePeriod();

        //We don't announce joins to the pvp server.
        event.setJoinMessage("");
    }

    private void checkGracePeriod() {
        //Grace period is when the the first player joins after the server was empty before bungee will spit out chat
        //from other players. This is to prevent a huge spam of chat buildup when the server's been empty for awhile.
        if (plugin.server.getOnlinePlayers().size() != 1) return;

        //This is the first player on, set a grace period.
        log.info("Activating chat grace period");
        plugin.bungee.muteChatFromLoka = true;

        //After a second, grace period can terminate.
        plugin.getServer().getScheduler().runTaskLater(plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("Deactivating chat grace period");
                        plugin.bungee.muteChatFromLoka = false;
                    }
                }, 20
        );
    }
}
