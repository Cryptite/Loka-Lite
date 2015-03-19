package com.cryptite.lite.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playCustomSound(Player p, String sound) {
        playCustomSound(p, sound, 1f);
    }

    public static void playCustomSound(Player p, String sound, Float volume) {
        //Also play for the player
        p.playSound(p.getLocation(), sound, volume, 1);
    }

    public static void playWorldCustomSound(Location l, String sound, float volume) {
        playWorldCustomSound(l, sound, 15, volume);
    }

    public static void playWorldCustomSound(Location l, String sound) {
        playWorldCustomSound(l, sound, 15, 1);
    }

    private static void playWorldCustomSound(Location l, String sound, int radius, float volume) {
        //Get all nearby entities within radius blocks
        for (Player p : l.getWorld().getPlayers()) {
            if (!p.getLocation().getWorld().equals(l.getWorld())) continue;

            double distance = p.getLocation().distance(l);
            if (distance <= radius) {
                p.playSound(l, sound, volume, 1);
            }
        }
    }
}
