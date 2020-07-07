package com.cryptite.lite;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class ChatManager implements CommandExecutor {
    private final LokaLite plugin;

    public ChatManager(LokaLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (commandLabel.equalsIgnoreCase("p")) {
            if (args.length < 1) {
                player.sendMessage(GRAY + "Talk in global chat.");
                player.sendMessage(AQUA + "Usage: " +
                        YELLOW + "/p <message>" + AQUA + ".");
                return true;
            }

            sendMessage(player.getName(), "public", args);
            return true;
        }
        return true;
    }

    public void sendMessage(String player, String channel, String[] args) {
        sendMessage(player, channel, assembleMessage(args));
    }

    public void sendMessage(String player, String channel, String message) {
        Account p = plugin.accounts.getAccount(player);
        switch (channel) {
            case "public":
                if (plugin.oldWorlds == null)
                    globalChatMessage(p, message);
                break;
            default:
                globalMessage(p, message);
                break;
        }
    }

    private String assembleMessage(String[] args) {
        StringBuilder b = new StringBuilder();
        for (String arg : args) {
            b.append(arg).append(" ");
        }
        return b.toString();
    }

    public void globalMessage(Account p, String message) {
        String chatMessage = DARK_GRAY + "[" + plugin.chatChannel + "] " + GOLD + p.getName() + WHITE + ": " + message;
        for (Player player : plugin.server.getOnlinePlayers()) {
            if (player == null) continue;
            player.sendMessage(chatMessage);
        }
        System.out.println(chatMessage);
    }

    public void globalChatMessage(Account p, String message) {
        StringBuilder chatMessage = new StringBuilder();

        if (p.getRank() != null && p.getRank().equals("Old One")) {
            chatMessage.append(ChatColor.RED).append("Old One");
        } else {
            chatMessage.append(ChatColor.GOLD).append(p.getRank());
        }
        String playerColor = ChatColor.WHITE + p.getName();

        chatMessage.append(ChatColor.WHITE).append(" ").append(playerColor).append(ChatColor.WHITE);
        chatMessage.append(": ").append(message);

        for (Player player : plugin.server.getOnlinePlayers()) {
            if (player == null) continue;
            player.sendMessage(chatMessage.toString());
        }
    }
}
