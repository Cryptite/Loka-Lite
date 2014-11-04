package com.cryptite.lite;

import com.cryptite.lite.bungee.Bungee;
import com.cryptite.lite.db.Town;
import com.cryptite.lite.listeners.*;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.bukkit.ChatColor.GRAY;

public class LokaLite extends JavaPlugin implements CommandExecutor {
    private final Logger log = Logger.getLogger("LokaLite");

    //Plugin or Server-based variables
    public Server server;
    public BukkitScheduler scheduler;

    public PluginManager pm;

    public final Map<String, Account> players = new HashMap<>();
    private Map<String, Town> towns = new HashMap<>();

    //Game variables
    public World world;
    public final List<String> playersToReturn = new ArrayList<>();

    //Misc
    public Bungee bungee;
    public Location spawn;
    public ChatManager chat;
    public DB db;
    public ConfigFile config;
    private Status status;
    public String serverName = "build";

    public void onEnable() {
        pm = this.getServer().getPluginManager();
        server = getServer();
        scheduler = server.getScheduler();
        config = new ConfigFile(this, "config.yml");

        //Bungee proxy stuff
        bungee = new Bungee(this);
        pm.registerEvents(bungee, this);

        world = server.getWorld("world");
        spawn = new Location(world, 415, 44, 660);

        chat = new ChatManager(this, bungee);
        getCommand("p").setExecutor(chat);
        getCommand("t").setExecutor(chat);
        getCommand("a").setExecutor(chat);
        getCommand("o").setExecutor(chat);

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new PlayerChatListener(this), this);

        initDbPool();

        status = new Status(this);
        pm.registerEvents(status, this);

        //Config related stuff
        if (!Boolean.parseBoolean(config.get("settings.build", false))) {
            System.out.println("[SETTINGS] Interactions disabled");
            pm.registerEvents(new PlayerInteractListener(), this);
        } else {
            System.out.println("[SETTINGS] Interactions allowed");
        }

        if (!Boolean.parseBoolean(config.get("settings.pvp", false))) {
            System.out.println("[SETTINGS] PvP disabled");
            pm.registerEvents(new PlayerDamageListener(), this);
        } else {
            System.out.println("[SETTINGS] PvP allowed");
        }

        serverName = config.get("servername", "build");

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
    }

    private void initDbPool() {
        try {
            MongoClient mongoClient = new MongoClient("iron.minecraftarium.com", 27017);
            db = mongoClient.getDB("loka");
            System.out.println("[DB] Connected to Master DB");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
                             String commandLabel, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }

        if (commandLabel.equalsIgnoreCase("pvp")) {
            if (args[0].equalsIgnoreCase("generic")) {
            }
        } else if (commandLabel.equalsIgnoreCase("leave")) {
            bungee.sendPlayer(player);
        } else if (commandLabel.equalsIgnoreCase("shutdown")) {
            for (Player pl : server.getOnlinePlayers()) {
                pl.sendMessage(GRAY + "This server is restarting for maintenance.");
                bungee.sendPlayer(pl);
            }
        }
        return true;
    }

    public Account getAccount(String name) {
        if (players.containsKey(name)) {
            return players.get(name);
        } else {
            Account p = new Account(this, name);
            p.load();
            players.put(name, p);
            return p;
        }
    }

    public Town getTown(String name) {
        if (towns.containsKey(name)) {
            return towns.get(name);
        } else {
            Town t = new Town(name);
            t.load();
            towns.put(name, t);
            return t;
        }
    }
}
