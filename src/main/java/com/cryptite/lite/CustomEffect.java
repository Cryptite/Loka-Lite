package com.cryptite.lite;

import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public enum CustomEffect {
    HUGE_EXPLOSION("hugeexplosion"),
    LARGE_EXPLODE("largeexplode"),
    FIREWORKS_SPARK("fireworksSpark"),
    BUBBLE("bubble"),
    SUSPEND("suspend"),
    DEPTH_SUSPEND("depthSuspend"),
    TOWN_AURA("townaura"),
    CRIT("crit"),
    MAGIC_CRIT("magicCrit"),
    MOB_SPELL("mobSpell"),
    MOB_SPELL_AMBIENT("mobSpellAmbient"),
    SPELL("spell"),
    INSTANT_SPELL("instantSpell"),
    WITCH_MAGIC("witchMagic"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    EXPLODE("explode"),
    FLAME("flame"),
    LAVA("lava"),
    FOOTSTEP("footstep"),
    SPLASH("splash"),
    LARGE_SMOKE("largesmoke"),
    CLOUD("cloud"),
    RED_DUST("reddust"),
    SNOWBALL_POOF("snowballpoof"),
    DRIP_WATER("dripWater"),
    DRIP_LAVA("dripLava"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    ANGRY_VILLAGER("angryVillager"),
    HAPPY_VILLAGER("happyVillager"),
    ICONCRACK("iconcrack_"),
    TILECRACK("tilecrack_");

    private final Logger log = Logger.getLogger("Artifact-Tower");
    private final String particleName;

    CustomEffect(final String particleName) {
        this.particleName = particleName;
    }

    public void createEffect(Player p, Location location,
                             float xOffset, float yOffset, float zOffset, float effectSpeed,
                             int amountOfParticles) {
        if (!p.getWorld().equals(location.getWorld())) return;

        // Make an instance of the packet!
        PacketPlayOutWorldParticles sPacket = new PacketPlayOutWorldParticles();

        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();

        for (Field field : sPacket.getClass().getDeclaredFields()) {
            try {
                // Get those fields we need to be accessible!
                field.setAccessible(true);
                String fieldName = field.getName();
                // Set them to what we want!
                switch (fieldName) {
                    case "a":
                        field.set(sPacket, particleName);
                        break;
                    case "b":
                        field.setFloat(sPacket, x);
                        break;
                    case "c":
                        field.setFloat(sPacket, y);
                        break;
                    case "d":
                        field.setFloat(sPacket, z);
                        break;
                    case "e":
                        field.setFloat(sPacket, xOffset);
                        break;
                    case "f":
                        field.setFloat(sPacket, yOffset);
                        break;
                    case "g":
                        field.setFloat(sPacket, zOffset);
                        break;
                    case "h":
                        field.setFloat(sPacket, effectSpeed);
                        break;
                    case "i":
                        field.setInt(sPacket, amountOfParticles);
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        CraftPlayer cp = (CraftPlayer) p;
        cp.getHandle().playerConnection.sendPacket(sPacket);
    }
}