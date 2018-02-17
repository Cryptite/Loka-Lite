package com.cryptite.lite;

import com.cryptite.lite.db.Chat;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class ChatManager implements CommandExecutor {
    private static final String CHAT_TOPIC = "chat";
    private LokaLite plugin;

    public ChatManager(LokaLite plugin) {
        this.plugin = plugin;
        plugin.mq.subscribe(CHAT_TOPIC, Chat.class, this::onChatReceived);
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

            sendMessage(player.getName(), "public", args, true);
            return true;
        } else if (commandLabel.equalsIgnoreCase("t")) {
            if (args.length < 1) {
                player.sendMessage(GRAY + "Talk in town chat.");
                player.sendMessage(AQUA + "Usage: " +
                        YELLOW + "/t <message>" + AQUA + ".");
                return true;
            } else if (plugin.getAccount(player.getName()).town == null) {
                player.sendMessage(GRAY + "You must be in a town to do this");
                return true;
            }

            sendMessage(player.getName(), "town", args, true);
            return true;
        } else if (commandLabel.equalsIgnoreCase("a")) {
            if (args.length < 1) {
                player.sendMessage(GRAY + "Talk in alliance chat.");
                player.sendMessage(AQUA + "Usage: " +
                        YELLOW + "/a <message>" + AQUA + ".");
                return true;
            } else if (plugin.getAccount(player.getName()).town == null) {
                player.sendMessage(GRAY + "You must be in a town to do this");
                return true;
            } else if (plugin.getAccount(player.getName()).town.alliance == null) {
                player.sendMessage(GRAY + "You must be in an alliance to do this");
                return true;
            }

            sendMessage(player.getName(), "alliance", args, true);

            return true;
        } else if (commandLabel.equalsIgnoreCase("o")) {
            if (args.length < 1) {
                player.sendMessage(GRAY + "Talk in admin chat.");
                player.sendMessage(AQUA + "Usage: " +
                        YELLOW + "/o <message>" + AQUA + ".");
                return true;
            } else if (!isAdmin(plugin.getAccount(player.getName()))) return true;

            sendMessage(player.getName(), "admin", args, true);

            return true;
        }
        return true;
    }

    private void onChatReceived(Chat chat) {
        sendMessage(chat, false);
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
                if (plugin.oldWorlds == null) {
                    globalChatMessage(p, message);
                } else {
                    return;
                }
                break;
            case "town":
//                townChatMessage(p, message);
                break;
            case "alliance":
//                allianceChatMessage(p, message);
                break;
            case "admin":
                adminChatMessage(p, message);
                break;
            default:
                globalMessage(p, message);
                break;
        }

        if (outgoing) {
            //Send to network
            Chat chat = new Chat(player, channel, message);
            plugin.mq.publish(CHAT_TOPIC, new Gson().toJson(chat));
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
        String chatMessage = DARK_GRAY + "[" + plugin.chatChannel + "] " + GOLD + p.name + WHITE + ": " + message;
        for (Player player : plugin.server.getOnlinePlayers()) {
            if (player == null) continue;
            player.sendMessage(chatMessage);
        }
        System.out.println(chatMessage);
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
        return (p.rank.equalsIgnoreCase("Guardian") || p.rank.equalsIgnoreCase("Elder") || p.rank.equalsIgnoreCase("OldOne"));
    }
}
