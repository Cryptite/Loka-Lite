package com.cryptite.lite.db;

import com.cryptite.lite.Account;
import com.cryptite.lite.LokaLite;
import com.cryptite.lite.utils.UUIDFetcher;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class DBData {
    public UUID uuid;
    private LokaLite plugin;
    private DBCollection collection;
    private DBObject query;
    DBObject data;

    public DBData(Account p, LokaLite plugin) {
        this.plugin = plugin;
        collection = plugin.db.getCollection("players");

        //We're just gonna query by name for now until the UUID update actually becomes a thing
        //If we're not the live server, then the UUID for the player is wrong so we grab it this way i guess...
        if (p.uuid == null) {
            if (p.getPlayer() == null) {
                try {
                    query = new BasicDBObject("uuid", getUUID(p.name));
                    System.out.println("[DB] " + p.name + " looking up UUID...");
                } catch (Exception e) {
                    query = new BasicDBObject("name", p.name);
                    System.out.println("[DB] " + p.name + " failed! Fallback on name. WATCH FOR THIS!");
                }
            } else {
                query = new BasicDBObject("uuid", p.getPlayer().getUniqueId());
                System.out.println("[DB] " + p.name + " getting UUID from player.");
            }
        } else {
            query = new BasicDBObject("uuid", p.uuid);
            uuid = p.uuid;
            System.out.println(ChatColor.GREEN + "[DB] has UUID already. This is best!");
        }

        DBCursor cursor = collection.find(query).limit(1);
        if (cursor.hasNext()) {
            data = cursor.next();

            if (uuid == null) {
                if (data.get("uuid") != null) {
                    uuid = UUID.fromString(data.get("uuid").toString());
                    System.out.println("[DB] UUID already fetched in the past, is: " + uuid);
                } else {
                    uuid = getUUID(p.name);
                }
            }
        } else {
            System.out.println("[DB] Attempting name-based fallback...");
            query = new BasicDBObject("name", p.name);
            cursor = collection.find(query).limit(1);
            if (cursor.hasNext()) {
                data = cursor.next();

                if (uuid == null) {
                    if (data.get("uuid") != null) {
                        uuid = UUID.fromString(data.get("uuid").toString());
                        System.out.println("[DB] UUID already fetched in the past, is: " + uuid);
                    } else {
                        uuid = getUUID(p.name);
                    }
                }
            } else {
                BasicDBObject newPlayer = new BasicDBObject()
                        .append("name", p.name);

                if (uuid == null) {
                    newPlayer.append("uuid", getUUID(p.name));
                } else {
                    newPlayer.append("uuid", uuid);
                }
                update(newPlayer);

                System.out.println("[DB] Created player: " + p.name);
            }
        }
    }

    public DBData(Town t) {
        plugin = (LokaLite) Bukkit.getPluginManager().getPlugin("LokaLite");

        BasicDBObject query = new BasicDBObject("name", t.name);

        DBCursor cursor = plugin.db.getCollection("towns").find(query);
        if (cursor.hasNext()) data = cursor.next();
    }

    private UUID getUUID(String name) {
        try {
            UUID uuid = UUIDFetcher.getUUIDOf(name);
            System.out.println("[DB] Retrieved UUID: " + uuid);
            return uuid;
        } catch (Exception e) {
            System.out.println("[DB] Could not retrieve uuid");
            e.printStackTrace();
        }
        return null;
    }

    private LokaLite getPlugin() {
        return (LokaLite) Bukkit.getPluginManager().getPlugin("LokaLite");
    }

    public int getInt(String key, Integer defaultValue) {
        try {
            return Integer.parseInt(get(key, defaultValue));
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String key, Integer defaultValue) {
        try {
            return Long.parseLong(get(key, defaultValue));
        } catch (Exception e) {
            return 0;
        }
    }

    public Map getMap(String key) {
        if (data == null || !data.containsField(key) || data.get(key) == null) return new HashMap<>();
        try {
            return (Map) data.get(key);
        } catch (Exception e) {
            System.out.println("[DB] Failed to get Map of key: " + key);
            return new HashMap<>();
        }
    }

    public List getList(String key) {
        if (data == null || !data.containsField(key) || data.get(key) == null) return new ArrayList<>();
        if (data.get(key).equals("") || data.get(key).equals("false") || data.get(key).equals("true"))
            return new ArrayList<>();

        try {
            return (List) data.get(key);
        } catch (Exception e) {
            System.out.println("[DB] Failed to get List of key: " + key);
            return new ArrayList<>();
        }
    }

    public String get(String key, Object defaultValue) {
        if (data == null) {
            if (defaultValue == null) {
                //Because I want strings back, I can't return a string called null, so manually doing it.
                return null;
            } else {
                //Return the variable default value as string.
                return defaultValue.toString();
            }
        }

        Object value = data.get(key);
        if (value != null) {
            return value.toString();
        } else {
            if (defaultValue == null) {
                //Because I want strings back, I can't return a string called null, so manually doing it.
                return null;
            } else {
                //Return the variable default value as string.
                return defaultValue.toString();
            }
        }
    }

    public void increment(String key, int amount) {
        update("$inc", new BasicDBObject(key, amount));
    }

    public void update(String key, Object value) {
        update("$set", new BasicDBObject(key, value));
    }

    public void update(BasicDBObject data) {
        update("$set", data);
    }

    public void push(String key, String value) {
        update("$push", new BasicDBObject(key, value));
    }

    public void remove(String key) {
        update("$unset", new BasicDBObject(key, 1));
    }

    public void delete() {
        if (collection.getName().endsWith("territories")) {
            collection.remove(query);
        }
    }

    private void update(String type, BasicDBObject updateData) {
        plugin.scheduler.runTaskAsynchronously(plugin, () -> {
            if (collection.find(query).limit(1).hasNext()) {
                //Exists, so update the value with the proper type (inc, set, etc)
                collection.update(query, new BasicDBObject().append(type, updateData));
            } else {
                //Otherwise we need to insert entirely.
                collection.insert(updateData);
            }
        });
    }

    public Boolean exists(String name) {
        //Select collection
        if (uuid == null) {
            return collection.find(new BasicDBObject("name", name)).hasNext();
        }
        return collection.find(query).hasNext();
    }
}
