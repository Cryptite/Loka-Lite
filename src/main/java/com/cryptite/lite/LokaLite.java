package com.cryptite.lite;

import com.cryptite.lite.bungee.Bungee;
import com.cryptite.lite.db.Town;
import com.cryptite.lite.listeners.*;
import com.cryptite.lite.modules.OldWorlds;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
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

import java.util.*;
import java.util.logging.Logger;

import static com.cryptite.lite.utils.LocationUtils.parseCoord;
import static com.mongodb.MongoCredential.createScramSha1Credential;
import static java.lang.Boolean.parseBoolean;
import static org.bukkit.ChatColor.GRAY;

public class LokaLite extends JavaPlugin implements CommandExecutor {
    public static final Logger log = Logger.getLogger("LokaLite");

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
    Status status;
    public String serverName = "build";
    public String chatChannel = "---";

    //Warps
    public Location sanya, ak, da, taan;
    public Location sanyaPlate, akPlate, daPlate, taanPlate;
    public OldWorlds oldWorlds;
    public MQ mq;

    public void onEnable() {
        pm = this.getServer().getPluginManager();
        server = getServer();
        scheduler = server.getScheduler();
        config = new ConfigFile(this, "config.yml");
        serverName = config.get("servername", "build");

        mq = new MQ(this, config.get("mqServer", "pvp.lokamc.com"));

        //Bungee proxy stuff
        bungee = new Bungee(this);
        pm.registerEvents(bungee, this);

        world = server.getWorld("spawn");
        spawn = new Location(world, -6.5, 64, -54.5);

        chat = new ChatManager(this);
        getCommand("p").setExecutor(chat);
        getCommand("t").setExecutor(chat);
        getCommand("a").setExecutor(chat);
        getCommand("o").setExecutor(chat);

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new PlayerChatListener(this), this);
        pm.registerEvents(new PlayerWorldListener(this), this);

        initDbPool();

        status = new Status(this);
        pm.registerEvents(status, this);

        //Config related stuff
        if (!parseBoolean(config.get("settings.build", false))) {
            System.out.println("[SETTINGS] Interactions disabled");
            pm.registerEvents(new PlayerInteractListener(this), this);
            pm.registerEvents(new BlockListener(this), this);
        } else {
            System.out.println("[SETTINGS] Interactions allowed");
        }

        if (!parseBoolean(config.get("settings.pvp", false))) {
            System.out.println("[SETTINGS] PvP disabled");
            pm.registerEvents(new PlayerDamageListener(), this);
        } else {
            System.out.println("[SETTINGS] PvP allowed");
        }

        String module = config.get("module", "");
        if ("oldworlds".equals(module)) {
            sanya = new Location(server.getWorld("world_artifact"), -64, 81, 114, -90, 0);
            ak = new Location(server.getWorld("world_loka"), 329.5, 117, -139.5);
            da = new Location(server.getWorld("world3"), -9144, 101, 4402);
            taan = new Location(server.getWorld("fourthworld"), -74.5, 111, -74.5);

            sanyaPlate = new Location(world, 7, 63, -27);
            akPlate = new Location(world, -7, 63, -13);
            daPlate = new Location(world, -21, 63, -27);

            oldWorlds = new OldWorlds(this);
            pm.registerEvents(oldWorlds, this);
        }

        chatChannel = config.get("chat", "---");
        mq.start();

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        mq.close();
        status.setReady(false);

        for (Player p : server.getOnlinePlayers()) {
            p.sendMessage(GRAY + "This server is restarting for maintenance.");
            bungee.sendPlayer(p);
        }
    }

    private void initDbPool() {
        MongoClient mongoClient;
        String username = config.get("db.user", "");
        String pass = config.get("db.password", "");
        MongoCredential credential = createScramSha1Credential(username, "loka", pass.toCharArray());
        mongoClient = new MongoClient(new ServerAddress("play.lokamc.com"), Arrays.asList(credential));
        db = mongoClient.getDB("loka");
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

            } else if (args[0].equalsIgnoreCase("tp")) {
                player.teleport(parseCoord(this, args[1]));
            }
        } else if (commandLabel.equalsIgnoreCase("leave")) {
            bungee.sendPlayer(player);
        } else if (commandLabel.equalsIgnoreCase("hub")) {
            if (player != null && spawn != null) {
                player.teleport(spawn);
                player.setAllowFlight(false);
            }
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
