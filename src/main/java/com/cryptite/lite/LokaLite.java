package com.cryptite.lite;

import com.cryptite.lite.listeners.*;
import com.cryptite.lite.modules.OldWorlds;
import com.lokamc.ConfigFile;
import com.lokamc.LokaCore;
import com.lokamc.accounts.AccountManager;
import com.lokamc.accounts.BaseAccountData;
import com.mongodb.reactivestreams.client.MongoDatabase;
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

import java.util.Collections;

import static com.lokamc.LokaCore.bungee;
import static org.bukkit.ChatColor.GRAY;

public class LokaLite extends JavaPlugin implements CommandExecutor {
    //Plugin or Server-based variables
    public Server server;
    public BukkitScheduler scheduler;

    public PluginManager pm;

    //Game variables
    public World world;

    //Misc
    public Location spawn;
    public MongoDatabase db;
    public String chatChannel = "---";

    //Warps
    public Location sanya, ak, da, taan;
    public Location sanyaPlate, akPlate, daPlate, taanPlate;
    public OldWorlds oldWorlds;
    public ConfigFile config;
    public AccountManager<BaseAccountData, BaseAccountData> accounts;

    @Override
    public void onEnable() {
        pm = this.getServer().getPluginManager();
        server = getServer();
        scheduler = server.getScheduler();
        config = new ConfigFile(this, "config.yml");

        world = server.getWorld("spawn");
        spawn = new Location(world, -6.5, 64, -54.5);

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(), this);
        pm.registerEvents(new PlayerWorldListener(this), this);

        db = LokaCore.connectDB(Collections.emptyList(), Collections.singletonList(BaseAccountData.class));
        accounts = new AccountManager<>(db, "players", BaseAccountData.class, data -> new BaseAccountData(db, data, "players"));

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

    @Override
    public void onDisable() {
        for (Player p : server.getOnlinePlayers()) {
            p.sendMessage(GRAY + "This server is restarting for maintenance.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }

        if (commandLabel.equalsIgnoreCase("leave")) {
            bungee.sendPlayer(player, "loka");
        } else if (commandLabel.equalsIgnoreCase("hub")) {
            if (player != null && spawn != null) {
                player.teleport(spawn);
                player.setAllowFlight(false);
            }
        }
        return true;
    }
}
