package com.cryptite.lite;

import com.cryptite.lite.db.DBData;
import com.cryptite.lite.db.Town;
import com.mongodb.BasicDBObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Logger;

public class Account {
    private final Logger log = Logger.getLogger("Artifact-PvPPlayer");
    private final LokaLite plugin;

    public final String name;
    private final DBData data;
    public UUID uuid;
    public Town town;

    public String rank;

    public Account(LokaLite plugin, String name) {
        this.name = name;
        this.plugin = plugin;
        data = new DBData(this, plugin);
    }

    public void update(BasicDBObject dataObj) {
        data.update(dataObj);
    }

    public void update(String key, Object value) {
        data.update(key, value);
    }

    public void append(String key, String value) {
        data.push(key, value);
    }

    public void increment(String key) {
        increment(key, 1);
    }

    void increment(String key, int amount) {
        data.increment(key, amount);
    }

    private void remove(String key) {
        data.remove(key);
    }

    public Boolean exists() {
        return data.exists(name);
    }

    public void load() {
        if (name == null) return;

        uuid = data.uuid;

        rank = data.get("rank", rank);
        String town = data.get("town", null);
        if (town != null) this.town = plugin.getTown(town);
    }

    public void sendMessage(String message) {
        Player p = getPlayer();
        if (p != null && p.isOnline()) {
            p.sendMessage(message);
        }
    }

    public Location getLocation() {
        if (getPlayer() == null) return null;
        return getPlayer().getLocation();
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayerExact(name);
    }

    public Boolean isTownOwner() {
        return town != null && town.owner.equals(name);
    }
}
