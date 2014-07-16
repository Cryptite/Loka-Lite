package com.cryptite.lite.bungee;

import com.cryptite.lite.PvPPlayer;

@SuppressWarnings("FieldCanBeLocal")
public class PlayerStats {

    public final String name;
    boolean talentsSaved = true;
    public String rank;
    public String town;
    public String alliance;
    private int prowess = 0;
    private int arenarating = 1500;
    private int arenawins = 0;
    private int arenalosses = 0;
    private int arenarating2v2 = 1500;
    private int arenawins2v2 = 0;
    private int arenalosses2v2 = 0;
    private int valleyScore = 0;
    private int valleyWins = 0;
    private int valleyLosses = 0;
    private int valleyKills = 0;
    private int valleyDeaths = 0;
    private int valleyCaps = 0;
    private int overloadScore = 0;
    private int overloadWins = 0;
    private int overloadLosses = 0;
    private int overloadKills = 0;
    private int overloadDeaths = 0;
    private int overloadCaps = 0;
    private int arrowShots = 0;
    private int arrowHits = 0;

    public PlayerStats(PvPPlayer p) {
        this.prowess = p.prowess;
        this.arenarating = p.arenarating;
        this.arenawins = p.arenawins;
        this.arenalosses = p.arenalosses;
        this.arenarating2v2 = p.arenarating2v2;
        this.arenawins2v2 = p.arenawins2v2;
        this.arenalosses2v2 = p.arenalosses2v2;
        this.valleyScore = p.valleyScore;
        this.valleyWins = p.valleyWins;
        this.valleyLosses = p.valleyLosses;
        this.valleyKills = p.valleyKills;
        this.valleyDeaths = p.valleyDeaths;
        this.valleyCaps = p.valleyCaps;
        this.overloadScore = p.overloadScore;
        this.overloadWins = p.overloadWins;
        this.overloadLosses = p.overloadLosses;
        this.overloadKills = p.overloadKills;
        this.overloadDeaths = p.overloadDeaths;
        this.overloadCaps = p.overloadOverloads;
        this.arrowShots = p.arrowsFired;
        this.arrowHits = p.arrowHits;
        this.name = p.name;
        this.talentsSaved = p.talentsSaved;
    }
}
