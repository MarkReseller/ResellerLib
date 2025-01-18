package lv.reseller.netherwars.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Fireball;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;

public class FireballUtil {

    private static Field fieldFireballDirX;
    private static Field fieldFireballDirY;
    private static Field fieldFireballDirZ;

    private static Method craftFireballHandle;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String nmsFireball = "net.minecraft.server." + version + "EntityFireball";
        String craftFireball = "org.bukkit.craftbukkit." + version + "entity.CraftFireball";
        try {
            Class<?> fireballClass = Class.forName(nmsFireball);

            //should be accessible by default.
            fieldFireballDirX = fireballClass.getDeclaredField("dirX");
            fieldFireballDirY = fireballClass.getDeclaredField("dirY");
            fieldFireballDirZ = fireballClass.getDeclaredField("dirZ");

            craftFireballHandle = Class.forName(craftFireball).getDeclaredMethod("getHandle");

        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static void setDirection(Fireball fireball, Vector direction) {
        try {
            Object handle = craftFireballHandle.invoke(fireball);
            fieldFireballDirX.set(handle, direction.getX() * 0.10D);
            fieldFireballDirY.set(handle, direction.getY() * 0.10D);
            fieldFireballDirZ.set(handle, direction.getZ() * 0.10D);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static void launchFireball(Player player) {
        LargeFireball f = player.launchProjectile(LargeFireball.class);
        f.setYield(3);
        f.setIsIncendiary(false);
        FireballUtil.setDirection(f, player.getEyeLocation().getDirection());
    }

}
