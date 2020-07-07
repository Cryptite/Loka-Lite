package com.cryptite.lite;

import com.cryptite.lite.listeners.*;
import com.cryptite.lite.modules.OldWorlds;
import com.lokamc.ConfigFile;
import com.lokamc.accounts.AccountManager;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.cryptite.lite.utils.LocationUtils.parseCoord;
import static com.lokamc.LokaCore.baseCodecs;
import static com.lokamc.LokaCore.bungee;
import static com.mongodb.MongoCredential.createScramSha1Credential;
import static java.util.Collections.singletonList;
import static org.bukkit.ChatColor.GRAY;

public class LokaLite extends JavaPlugin implements CommandExecutor {
    public static final Logger log = Logger.getLogger("LokaLite");

    //Plugin or Server-based variables
    public Server server;
    public BukkitScheduler scheduler;

    public PluginManager pm;

    //Game variables
    public World world;
    public final List<String> playersToReturn = new ArrayList<>();

    //Misc
    public Location spawn;
    public ChatManager chat;
    public MongoDatabase db;
    public String serverName = "build";
    public String chatChannel = "---";

    //Warps
    public Location sanya, ak, da, taan;
    public Location sanyaPlate, akPlate, daPlate, taanPlate;
    public OldWorlds oldWorlds;
    public ConfigFile config;
    public AccountManager<AccountData, Account> accounts;

    public void onEnable() {
        pm = this.getServer().getPluginManager();
        server = getServer();
        scheduler = server.getScheduler();
        config = new ConfigFile(this, "config.yml");
        serverName = config.get("servername", "build");

        world = server.getWorld("spawn");
        spawn = new Location(world, -6.5, 64, -54.5);

        chat = new ChatManager(this);
        getCommand("p").setExecutor(chat);

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new PlayerChatListener(this), this);
        pm.registerEvents(new PlayerWorldListener(this), this);

        initDbPool();
        accounts = new AccountManager<>(db, "players", AccountData.class, data -> new Account(this, data));

        //Config related stuff
        if (!config.getBool("settings.build", false)) {
            System.out.println("[SETTINGS] Interactions disabled");
            pm.registerEvents(new PlayerInteractListener(this), this);
            pm.registerEvents(new BlockListener(this), this);
        } else {
            System.out.println("[SETTINGS] Interactions allowed");
        }

        if (!config.getBool("settings.pvp", false)) {
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

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        for (Player p : server.getOnlinePlayers()) {
            p.sendMessage(GRAY + "This server is restarting for maintenance.");
//            bungee.sendPlayer(p);
        }
    }

    private void initDbPool() {
        ConfigFile config = new ConfigFile(this, "config.yml");
        MongoClient mongoClient;
        String username = config.get("db.user", "");
        String pass = config.get("db.password", "");
        MongoCredential credential = createScramSha1Credential(username, "loka", pass.toCharArray());

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(baseCodecs),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .register(AccountData.class)
                        .build()),
                MongoClients.getDefaultCodecRegistry());

        String host = config.get("db.host", "");
        MongoClientSettings options = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(singletonList(new ServerAddress(host))))
                .codecRegistry(pojoCodecRegistry)
                .credential(credential)
                .build();

        System.out.println("[DB] Connecting to " + host);
        mongoClient = MongoClients.create(options);
        db = mongoClient.getDatabase("loka");
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
            bungee.sendPlayer(player, "loka");
        } else if (commandLabel.equalsIgnoreCase("hub")) {
            if (player != null && spawn != null) {
                player.teleport(spawn);
                player.setAllowFlight(false);
            }
//        } else if (commandLabel.equalsIgnoreCase("shutdown")) {
//            for (Player pl : server.getOnlinePlayers()) {
//                pl.sendMessage(GRAY + "This server is restarting for maintenance.");
//                bungee.sendPlayer(pl);
//            }
        }
        return true;
    }
}
