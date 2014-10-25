package com.cryptite.lite.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class Town {
    public String owner, name, tag, alliance;
    private Map<String, Integer> memberLevels = new HashMap<>();
    private Map<String, List<String>> ranks = new HashMap<>();

    public Town(String name) {
        this.name = name;
    }

    public void load() {
        DBData dbData = new DBData(this);

        owner = dbData.get("owner", null);
        ranks.putAll((Map) dbData.data.get("ranks"));
        memberLevels.putAll((Map) dbData.data.get("members"));
        tag = dbData.get("tag", name);
        alliance = dbData.get("alliance", null);

        System.out.println("[DB] Loaded " + name + " with " + memberLevels.size() + " members.");
    }

    public String getRank(String player) {
        for (String rank : ranks.keySet()) {
            if (ranks.get(rank).contains(player)) return rank;
        }

        return "";
    }

    public String getMemberLevelString(String player) {
        switch (getMemberLevel(player)) {
            case 1:
                return GRAY + "[" + GRAY + "I" + GRAY + "]";
            case 2:
                return GRAY + "[" + WHITE + "II" + GRAY + "]";
            case 3:
                return GRAY + "[" + GREEN + "III" + GRAY + "]";
            case 4:
                return GRAY + "[" + YELLOW + "Lord" + GRAY + "]";
        }

        return "";
    }

    private int getMemberLevel(String player) {
        if (!memberLevels.containsKey(player)) return 1;
        return memberLevels.get(player);
    }
}
