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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Status implements Listener {
    private static final String PLAYER_LIST_TOPIC = "playerList";
    private LokaLite plugin;
    private DB db;
    private BukkitTask updateID;

    private static class ServerList {
        private final String server;
        private final List<String> players;

        public ServerList(String server, List<String> players) {
            this.server = server;
            this.players = players;
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

        List<String> players = new ArrayList<>(plugin.server.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet()));
        updateID = plugin.scheduler.runTaskLaterAsynchronously(plugin,
                () -> plugin.mq.publish(PLAYER_LIST_TOPIC, new ServerList(plugin.serverName, players)), 20 * 3);
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
