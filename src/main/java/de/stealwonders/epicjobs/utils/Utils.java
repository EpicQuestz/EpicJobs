package de.stealwonders.epicjobs.utils;

import de.iani.playerUUIDCache.PlayerUUIDCache;
import de.iani.playerUUIDCache.PlayerUUIDCacheAPI;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class Utils {

    public static String serializeLocation(@NonNull final Location location) {
        return location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getYaw() + " " + location.getPitch();
    }

    public static Location deserializeLocation(final String input) {
        Location location = null;
        final String[] parts = input.split(" ");
        if (parts.length >= 6) {
            final World world = Bukkit.getWorld(parts[0]);
            if (world != null) {
                location = new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
            }
        }
        return location;
    }

    public static String shortenDescription(@NonNull Job job) {
        return StringUtils.abbreviate(job.getDescription(), 100);
    }

    public static String getPlayerHolderText(@Nullable UUID uuid) {
       final PlayerUUIDCacheAPI playerUUIDCacheAPI = EpicJobs.getPlayerUuidCache();
        if (playerUUIDCacheAPI != null) {
            return uuid != null ? playerUUIDCacheAPI.getPlayerFromNameOrUUID(uuid.toString()).getName() : "<none>";
        } else {
            return "Error fetching username!";
        }
    }

}
