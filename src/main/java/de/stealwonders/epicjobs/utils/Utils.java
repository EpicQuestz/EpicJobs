package de.stealwonders.epicjobs.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {

    public static String serializeLocation(final Location location) {
        return location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ();
    }

    public static Location deserializeLocation(final String input) {
        final String[] parts = input.split(" ");
        if (parts.length != 4) {
            return null;
        } else {
            final World world = Bukkit.getWorld(parts[0]);
            if (world != null) {
                return new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            } else {
                return null;
            }
        }
    }

}
