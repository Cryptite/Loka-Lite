package com.cryptite.lite;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Status implements Listener {
    private static final String PLAYER_LIST_TOPIC = "playerList";
    private LokaLite plugin;
    private DB db;
    private BukkitTask updateID;

    private class ServerList {
        private final String server;
        private final List<UUID> players;

        public ServerList(String server) {
            this.server = server;
            players = Bukkit.getOnlinePlayers().stream()
                    .map(Entity::getUniqueId)
                    .collect(Collectors.toList());
        }
    }

    public Status(LokaLite plugin) {
        this.plugin = plugin;
        db = plugin.db;

        //Update on startup, that way we're properly at 0
        updatePlayers();
    }

    void updatePlayers() {
        if (updateID != null) updateID.cancel();

        updateID = plugin.scheduler.runTaskLaterAsynchronously(plugin,
                () -> plugin.mq.publish(PLAYER_LIST_TOPIC, new ServerList(plugin.serverName)), 20 * 3);
    }

    void setReady(boolean ready) {
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
        updatePlayers();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        updatePlayers();
    }
}
