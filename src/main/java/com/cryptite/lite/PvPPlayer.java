package com.cryptite.lite;

import com.cryptite.lite.bungee.PlayerStats;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PvPPlayer {
    private final Logger log = Logger.getLogger("Artifact-PvPPlayer");

    private final LokaLite plugin;
    public String town;
    public String alliance;
    public final String name;
    public String rank;
    public Boolean townOwner = false;
    public final ConfigFile config;
    public boolean talentsSaved = false;

    //Stats
    public int arrowHits = 0;
    public int arrowsFired = 0;
    public int arenarating = 1500;
    public int arenarating2v2 = 1500;
    public int highestrating = 1500;
    public int highestrating2v2 = 1500;
    public int arenawins = 0;
    public int arenawins2v2 = 0;
    public int arenalosses = 0;
    public int arenalosses2v2 = 0;
    public int streak = 0;
    public int streak2v2 = 0;
    public int overloadKills = 0;
    public int overloadDeaths = 0;
    public int overloadOverloads = 0;
    public int overloadWins = 0;
    public int overloadLosses = 0;
    public final int overloadScore = 0;
    public int valleyKills = 0;
    public int valleyDeaths = 0;
    public int valleyCaps = 0;
    public int valleyWins = 0;
    public int valleyLosses = 0;
    public int valleyScore = 0;
    private long deserterTime = 0;
    public int prowess = 0;

    //Info notifications2
    private Boolean info_firstTimeArena = false;
    private Boolean info_firstTimeBG = false;

    //Talents
    public final Map<ItemStack, Integer> inventoryOrder = new HashMap<>();

    public PvPPlayer(LokaLite plugin, String name) {
        this.name = name;
        this.config = new ConfigFile(plugin, "players/" + name + ".yml");
        this.plugin = plugin;
    }

    public void save() {
        config.set("name", name);
        config.set("rank", rank);
        config.set("townowner", townOwner);
        config.set("arrowhits", arrowHits);
        config.set("arrowsfired", arrowsFired);
        config.set("arenarating", arenarating);
        config.set("arenarating2v2", arenarating2v2);
        config.set("arenawins", arenawins);
        config.set("arenalosses", arenalosses);
        config.set("arenawins2v2", arenawins2v2);
        config.set("arenalosses2v2", arenalosses2v2);
        config.set("valleyKills", valleyKills);
        config.set("valleyDeaths", valleyDeaths);
        config.set("valleyCaps", valleyCaps);
        config.set("valleyWins", valleyWins);
        config.set("valleyLosses", valleyLosses);
        config.set("overloadKills", overloadKills);
        config.set("overloadDeaths", overloadDeaths);
        config.set("overloadOverloads", overloadOverloads);
        config.set("overloadWins", overloadWins);
        config.set("overloadLosses", overloadLosses);
        config.set("streak", streak);
        config.set("streak2v2", streak2v2);
        config.set("deserterTime", deserterTime);
        config.set("highestrating", highestrating);
        config.set("highestrating2v2", highestrating2v2);
        config.set("info_firstTimeArena", info_firstTimeArena);
        config.set("info_firstTimeBG", info_firstTimeBG);
        config.save();

        //Send their updated stats along with the player
        PlayerStats stats = new PlayerStats(this);
        plugin.bungee.sendMessage(new net.minecraft.util.com.google.gson.Gson().toJson(stats), "PlayerUpdate");
        //Saving also updates Loka with all the information.
    }

    public void load() {
        rank = config.get("rank", rank);
        townOwner = Boolean.parseBoolean(config.get("townowner", townOwner));
//        prowess = Integer.parseInt(config.get("prowess", prowess));
        arrowHits = Integer.parseInt(config.get("arrowhits", arrowHits));
        arrowsFired = Integer.parseInt(config.get("arrowsfired", arrowsFired));
        arenarating = Integer.parseInt(config.get("arenarating", arenarating));
        arenarating2v2 = Integer.parseInt(config.get("arenarating2v2", arenarating2v2));
        arenawins = Integer.parseInt(config.get("arenawins", arenawins));
        arenawins2v2 = Integer.parseInt(config.get("arenawins2v2", arenawins2v2));
        arenalosses = Integer.parseInt(config.get("arenalosses", arenalosses));
        arenalosses2v2 = Integer.parseInt(config.get("arenalosses2v2", arenalosses2v2));
        streak = Integer.parseInt(config.get("streak", streak));
        streak2v2 = Integer.parseInt(config.get("streak2v2", streak2v2));
        highestrating = Integer.parseInt(config.get("highestrating", highestrating));
        highestrating2v2 = Integer.parseInt(config.get("highestrating2v2", highestrating));
        valleyKills = Integer.parseInt(config.get("valleyKills", valleyKills));
        valleyDeaths = Integer.parseInt(config.get("valleyDeaths", valleyDeaths));
        valleyCaps = Integer.parseInt(config.get("valleyCaps", valleyCaps));
        valleyWins = Integer.parseInt(config.get("valleyWins", valleyWins));
        valleyLosses = Integer.parseInt(config.get("valleyLosses", valleyLosses));
        overloadKills = Integer.parseInt(config.get("overloadKills", overloadKills));
        overloadDeaths = Integer.parseInt(config.get("overloadDeaths", overloadDeaths));
        overloadOverloads = Integer.parseInt(config.get("overloadOverloads", overloadOverloads));
        overloadWins = Integer.parseInt(config.get("overloadWins", overloadWins));
        overloadLosses = Integer.parseInt(config.get("overloadLosses", overloadLosses));
        deserterTime = Long.parseLong(config.get("deserterTime", deserterTime));
        info_firstTimeArena = Boolean.parseBoolean(config.get("info_firstTimeArena", info_firstTimeArena));
        info_firstTimeBG = Boolean.parseBoolean(config.get("info_firstTimeBG", info_firstTimeBG));
    }

    void loadInventoryOrder() {
        inventoryOrder.clear();
        if (config.configFile.exists() && config.getAll("inventoryorder", null) != null) {
            for (String item : config.getAll("inventoryorder", null)) {
                int slot = Integer.parseInt(config.get("inventoryorder." + item, null));
                ItemStack i;
                if (item.contains("-HEAL")) {
                    i = new ItemStack(Material.POTION);
                    i.setDurability((short) 16389);
                } else if (item.contains("-HARM")) {
                    i = new ItemStack(Material.POTION);
                    i.setDurability((short) 16428);
                } else {
                    i = new ItemStack(Material.valueOf(item));
                }
                inventoryOrder.put(i, slot);
            }
        }
    }

    public Location getLocation() {
        if (getPlayer() == null) return null;
        return getPlayer().getLocation();
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayerExact(name);
    }

}
