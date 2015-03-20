package com.cryptite.lite;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.stream.Collectors;

public class Status implements Listener {
    private LokaLite plugin;
    private DB db;
    private BukkitTask updateID;

    public Status(LokaLite plugin) {
        this.plugin = plugin;
        db = plugin.db;

        //Update on startup, that way we're properly at 0
        updatePlayers(true);
    }

    public void updatePlayers(boolean startup) {
        if (updateID != null) updateID.cancel();

        updateID = plugin.scheduler.runTaskLater(plugin, () -> {
            DBCollection coll = db.getCollection("servers");
            BasicDBObject query = new BasicDBObject("server", plugin.serverName);
            BasicDBObject data = new BasicDBObject("server", plugin.serverName)
                    .append("players",
                            plugin.server.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

            if (startup) data.append("available", true);

            if (coll.find(query).hasNext()) {
                coll.update(query, new BasicDBObject().append("$set", data));
            } else {
                coll.insert(data);
            }
        }, 20 * 3);
    }

    public void setReady(boolean ready) {
        DBCollection coll = db.getCollection("servers");
        BasicDBObject query = new BasicDBObject("server", plugin.serverName);
        BasicDBObject data = new BasicDBObject("server", plugin.serverName)
                .append("available", ready);
        if (coll.find(query).hasNext()) {
            coll.update(query, new BasicDBObject().append("$set", data));
        } else {
            coll.insert(data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayers(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        updatePlayers(false);
    }
}
