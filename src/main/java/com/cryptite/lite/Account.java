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
    public UUID uuid;
    public Town town;

    public String rank;

    public Account(LokaLite plugin, String name) {
        this.name = name;
        this.plugin = plugin;
    }

    public void update(BasicDBObject data) {
        new DBData(this).update(data);
    }

    public void update(String key, Object value) {
        new DBData(this).set(key, value);
    }

    public void increment(String key) {
        increment(key, 1);
    }

    public void increment(String key, int amount) {
        new DBData(this).increment(key, amount);
    }

    public void load() {
        if (name == null) return;

        DBData dbData = new DBData(this);
        uuid = dbData.uuid;

        rank = dbData.get("rank", rank);
        String town = dbData.get("town", null);
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
