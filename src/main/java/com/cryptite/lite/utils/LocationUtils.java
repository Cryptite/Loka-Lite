package com.cryptite.lite.utils;

import com.cryptite.lite.LokaLite;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LocationUtils {
    public static Set<Material> INVENTORY_MATERIALS = new HashSet<>(Arrays.asList(Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.DROPPER,
            Material.HOPPER,
            Material.FURNACE,
            Material.DISPENSER));

    public static void playCustomSound(Player p, String sound) {
        playCustomSound(p, sound, 1f);
    }

    public static void playCustomSound(Player p, String sound, Float volume) {
        //Get all nearby entities within 15 blocks
        for (Entity near : p.getNearbyEntities(15, 15, 15)) {
            if (!(near instanceof Player)) continue;

            ((Player) near).playSound(p.getLocation(), sound, volume, 1);
        }

        //Also play for the player
        p.playSound(p.getLocation(), sound, 1, 1);
    }

    public static void playWorldCustomSound(Location l, String sound, int radius) {
        for (Entity e : l.getWorld().getEntities()) {
            if (!(e instanceof Player)) continue;
            Player player = (Player) e;

            double distance = player.getLocation().distance(l);
            if (distance <= radius) {
                player.playSound(l, sound, 1, 1);
            }
        }
    }

    public static Location parseCoord(LokaLite plugin, String string) {
        if (string == null) return null;
        String[] elems = string.split(",");
        World world = plugin.server.getWorld(elems[0]);
        return new Location(world, Double.parseDouble(elems[1]),
                Double.parseDouble(elems[2]), Double.parseDouble(elems[3]),
                elems.length > 4 ? Float.parseFloat(elems[4]) : 0f,
                elems.length > 5 ? Float.parseFloat(elems[5]) : 0f);
    }

    public static String coordsToString(Location point) {
        return point.getWorld().getName() + "," +
                (int) point.getX() + "," +
                (int) point.getY() + "," +
                (int) point.getZ() + "," +
                (int) point.getYaw() + "," +
                (int) point.getPitch();
    }

    public static String coordsToStringBasic(Location point) {
        if (point == null) return null;

        String coords = point.getWorld().getName() + "," +
                point.getBlockX() + "," +
                point.getBlockY() + "," +
                point.getBlockZ();

        return coords;
    }

    public static Location getRandomLocation(Location source, int radius, int minimumRadius, List<Material> excludedTypes) {
        Random r = new Random();
        Location randomLocation = null;
        Material currentBlockType = Material.AIR;
        int spawnTries = 0;

        while (excludedTypes.contains(currentBlockType)) {
            double a = r.nextDouble() * 2 * Math.PI;
            double dist = (r.nextDouble() * radius) + minimumRadius;
            randomLocation = source.clone().add(dist * Math.sin(a), 0, dist * Math.cos(a));
            randomLocation.setY(source.getWorld().getHighestBlockYAt((int) randomLocation.getX(), (int) randomLocation.getZ()));

            currentBlockType = randomLocation.getBlock().getType();
            spawnTries++;

            //To avoid possible infinite looping
            if (spawnTries > 10) break;
        }

        return randomLocation;
    }

    public static List<Block> getBlocksFromRegion(Location p1, Location p2) {
        List<Block> blocks = new ArrayList<>();
        int minX = Math.min(p1.getBlockX(), p2.getBlockX());
        int maxX = Math.max(p1.getBlockX(), p2.getBlockX());
        int minY = Math.min(p1.getBlockY(), p2.getBlockY());
        int maxY = Math.max(p1.getBlockY(), p2.getBlockY());
        int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
        int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ());

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(p1.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static List<Block> getAdjacentBlocks(Block b, boolean searchUpDown) {
        List<Block> blocks = new ArrayList<>(Arrays.asList(b,
                b.getRelative(BlockFace.EAST),
                b.getRelative(BlockFace.NORTH),
                b.getRelative(BlockFace.SOUTH),
                b.getRelative(BlockFace.WEST)));
        if (searchUpDown) {
            blocks.add(b.getRelative(BlockFace.UP));
            blocks.add(b.getRelative(BlockFace.DOWN));
        }
        return blocks;
    }

    public static Inventory getChestInventory(Location chestLocation) {
        return getChestInventory(chestLocation.getBlock());
    }

    private static Inventory getChestInventory(Block block) {
        BlockState state = block.getState();
        if (state instanceof Chest) {
            return ((Chest) state).getInventory();
        } else if (state instanceof Furnace) {
            return ((Furnace) state).getInventory();
        } else if (state instanceof Dropper) {
            return ((Dropper) state).getInventory();
        } else if (state instanceof Hopper) {
            return ((Hopper) state).getInventory();
        } else if (state instanceof Dispenser) {
            return ((Dispenser) state).getInventory();
        }
        Chest chest = (Chest) block.getState();
        return chest.getInventory();
    }

    public static Map<Location, Map<String, Integer>> scanChests(Location p1, Location p2) {
        System.out.println("--- Scanning  for chests ---");
        List<Block> blocks = getBlocksFromRegion(p1, p2);
        System.out.println("--- Looking through " + blocks.size() + " blocks");
        Set<Block> chests = new HashSet<>();
        for (Block b : blocks) {
            if (!INVENTORY_MATERIALS.contains(b.getType())) continue;

            boolean doubleChest = false;
            if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                for (Block adjacent : getAdjacentBlocks(b, false)) {
                    if (adjacent.getType().equals(b.getType()) && chests.contains(adjacent)) {
                        doubleChest = true;
                        break;
                    }
                }
            }
            if (!doubleChest) {
                chests.add(b);
            }
        }

        Map<Location, Map<String, Integer>> chestMap = new HashMap<>();
        for (Block b : chests) {
            Map<String, Integer> items = new HashMap<>();
            for (ItemStack item : getChestInventory(b.getLocation())) {
                if (item == null || item.getType().equals(Material.AIR)) continue;
                String itemVal = item.getType().toString() + ":"
                        + (item.getType().getMaxDurability() == 0 ? item.getData().getData() : 0);

                items.put(itemVal, items.getOrDefault(itemVal, 0) + item.getAmount());
            }
            if (!items.isEmpty()) {
                System.out.println(items.size() + " items in chest at: " + coordsToString(b.getLocation()));
                chestMap.put(b.getLocation(), items);
            }
        }
        return chestMap;
    }
}
