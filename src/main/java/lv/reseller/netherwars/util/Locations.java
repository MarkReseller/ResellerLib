package lv.reseller.netherwars.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations {

    public static String asString(Location location) {
        return  location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    public static Location asLocation(String str) {
        String[] split = str.split(",");
        if(split.length == 4) {
            return new Location(
                    Bukkit.getWorld(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Double.parseDouble(split[3])
            );
        } else {
            return new Location(
                    Bukkit.getWorld(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Double.parseDouble(split[3]),
                    Float.parseFloat(split[4]),
                    Float.parseFloat(split[5])
            );
        }
    }

    public static String asStringPosition(Location location) {
        return  location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    public static Location asPosition(World world, String str) {
        String[] split = str.split(",");
        if(split.length == 3) {
            return new Location(
                    world,
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2])
            );
        } else {
            return new Location(
                    world,
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Float.parseFloat(split[3]),
                    Float.parseFloat(split[4])
            );
        }
    }


}
