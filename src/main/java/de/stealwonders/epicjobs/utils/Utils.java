package de.stealwonders.epicjobs.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import net.kyori.adventure.text.Component;
//import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class Utils {

    public static String serializeLocation(@NonNull final Location location) {
        return location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getYaw() + " " + location.getPitch();
    }

    public static @Nullable Location deserializeLocation(@NonNull final String input) {
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

//    public static String shortenDescription(@NonNull final Job job) {
//        return StringUtils.abbreviate(job.getDescription(), 100);
//    }

//    public static String getPlayerHolderText(@Nullable final UUID uuid) {
//       final PlayerUUIDCacheAPI playerUUIDCacheAPI = EpicJobs.getPlayerUuidCache();
//        if (playerUUIDCacheAPI != null) {
//            if (uuid != null) {
//                final CachedPlayer cachedPlayer = playerUUIDCacheAPI.getPlayerFromNameOrUUID(uuid.toString());
//                if (cachedPlayer != null) {
//                    return cachedPlayer.getName();
//                }
//            } else {
//                return "§oNone";
//            }
//        }
//        return "§oError fetching username!";
//    }

//    public static String color(final String msg) {
//        return ChatColor.translateAlternateColorCodes('&', msg);
//    }

//    public static ItemStack getSkull(final String base64, final String name) {
//        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
//        final ItemMeta itemMeta = itemStack.getItemMeta();
//        itemMeta.displayName(Component.text(name));
//        itemStack.setItemMeta(itemMeta);
//        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
//        final PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
//        final ProfileProperty profileProperty = new ProfileProperty("textures", base64);
//        playerProfile.getProperties().add(profileProperty);
//        skullMeta.setPlayerProfile(playerProfile);
//        itemStack.setItemMeta(skullMeta);
//        return itemStack;
//    }

}
