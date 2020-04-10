package de.stealwonders.epicjobs.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {

    public static String serializeLocation(final Location location) {
        return location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ();
    }

    public static Location deserializeLocation(final String input) {
        Location location = null;
        final String[] parts = input.split(" ");
        if (parts.length >= 4) {
            final World world = Bukkit.getWorld(parts[0]);
            if (world != null) {
                location = new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            }
        }
        return location;
    }

}
