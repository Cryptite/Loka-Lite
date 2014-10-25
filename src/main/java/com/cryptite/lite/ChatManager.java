package com.cryptite.lite;

import com.cryptite.lite.bungee.Bungee;
import com.cryptite.lite.db.Chat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class ChatManager {
    private LokaLite plugin;
    private Bungee bungee;

    public ChatManager(LokaLite plugin, Bungee bungee) {
        this.plugin = plugin;
        this.bungee = bungee;
    }

    public void sendMessage(Chat chat, Boolean outgoing) {
        sendMessage(chat.name, chat.channel, chat.message, outgoing);
    }

    public void sendMessage(String player, String channel, String[] args, Boolean outgoing) {
        sendMessage(player, channel, assembleMessage(args), outgoing);
    }

    public void sendMessage(String player, String channel, String message, Boolean outgoing) {
        Account p = plugin.getAccount(player);
        switch (channel) {
            case "public":
                globalChatMessage(p, message);
                break;
            case "town":
                townChatMessage(p, message);
                break;
            case "alliance":
                allianceChatMessage(p, message);
                break;
            case "admin":
                adminChatMessage(p, message);
        }

        if (outgoing) {
            //Send to network
            Chat chat = new Chat(player, channel, message);
            bungee.sendMessage("ALL", new Gson().toJson(chat), "Chat");
        }
    }

    private String assembleMessage(String[] args) {
        StringBuilder b = new StringBuilder();
        for (String arg : args) {
            b.append(arg).append(" ");
        }
        return b.toString();
    }

    public void globalChatMessage(Account p, String message) {
        StringBuilder chatMessage = new StringBuilder();

        if (p.isTownOwner()) {
            chatMessage.append(ChatColor.AQUA).append("[");
        } else {
            chatMessage.append(ChatColor.GRAY).append("[");
        }
        if (p.rank != null && p.rank.equals("Old One")) {
            chatMessage.append(ChatColor.RED).append("Old One");
        } else {
            chatMessage.append(ChatColor.GOLD).append(p.rank);
        }
        if (p.isTownOwner()) {
            chatMessage.append(ChatColor.AQUA).append("]");
        } else {
            chatMessage.append(ChatColor.GRAY).append("]");
        }
        String playerColor = ChatColor.WHITE + p.name;

        chatMessage.append(ChatColor.WHITE).append(" ").append(playerColor).append(ChatColor.WHITE);
        chatMessage.append(": ").append(message);

        for (Player player : plugin.server.getOnlinePlayers()) {
            if (player == null) continue;
            player.sendMessage(chatMessage.toString());
        }
    }

    void townChatMessage(Account p, String message) {
        String playerColor;
        if (p.isTownOwner()) {
            playerColor = AQUA + p.name;
        } else {
            playerColor = WHITE + p.name;
        }

        for (Player player : plugin.server.getOnlinePlayers()) {
            if (player == null) continue;

            Account pAccount = plugin.getAccount(player.getName());
            if (pAccount.town != null && pAccount.town.equals(p.town)) {
                p.sendMessage(GRAY + "[" +
                        AQUA + p.town.tag + GRAY + "] " + p.town.getMemberLevelString(pAccount.name) +
                        " " + p.town.getRank(pAccount.name) + WHITE + " " + playerColor +
                        WHITE + ": " + message);
            }
        }
    }

    void allianceChatMessage(Account p, String message) {
        String msg = GRAY + "[" + YELLOW + p.town.alliance + GRAY + "] ";
        msg += GRAY + "[" + AQUA + p.town.tag + GRAY + "] " + WHITE + p.name + ": " + message;

        for (Player player : plugin.server.getOnlinePlayers()) {
            if (player == null) continue;

            Account pAccount = plugin.getAccount(player.getName());
            if (pAccount.town == null || pAccount.town.alliance == null) continue;

            if (pAccount.town.alliance.equals(p.town.alliance)) {
                player.sendMessage(msg);
            }
        }
    }

    void adminChatMessage(Account player, String message) {
        String msg = GRAY + "[" + RED + "Admin" + GRAY + "] ";
        msg += RED + player.name + WHITE + ": " + message;
        for (Player p : plugin.server.getOnlinePlayers()) {
            if (p == null) continue;

            if (isAdmin(plugin.getAccount(p.getName()))) {
                p.sendMessage(msg);
            }
        }

        System.out.println(ChatColor.stripColor(msg));
    }

    private boolean isAdmin(Account p) {
        return (p.rank.equals("Guardian") || p.rank.equals("Elder") || p.rank.equals("OldOne"));
    }
}