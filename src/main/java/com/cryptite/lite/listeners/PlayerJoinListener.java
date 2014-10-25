package com.cryptite.lite.listeners;

import com.cryptite.lite.LokaLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.YELLOW;

public class PlayerJoinListener implements Listener {
    private final Logger log = Logger.getLogger("Artifact-Join");
    private final LokaLite plugin;

    public PlayerJoinListener(LokaLite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.teleport(plugin.spawn);
        player.setFlying(true);

        //Wipe Tab list stuff
//        TabAPI.setPriority(plugin, event.getPlayer(), -2);

        //Check for chat spam grace period
        checkGracePeriod();

        //We don't announce joins to the pvp server.
        event.setJoinMessage(YELLOW + event.getPlayer().getName() + GRAY + " has entered the ether.");

        player.sendMessage(GRAY + "You have entered the ether, a zone that exists in no specific time or place. You are free to explore, but your interactions with this plane are restricted.");
        player.sendMessage(GRAY + "You may use " + YELLOW + "/leave " + GRAY + "at any time to return to Loka.");

        plugin.getAccount(player.getName());
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
                () -> {
                    log.info("Deactivating chat grace period");
                    plugin.bungee.muteChatFromLoka = false;
                }, 20
        );
    }
}
