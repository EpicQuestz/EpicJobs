package com.epicquestz.epicjobs.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.Palette;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.project.Project;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class Utils {

    /**
     * Whether the sender may manage the project - its leader, one of its deputies, or a holder of
     * {@link CommandPermissions#BYPASS}. Non-players (console) are always allowed.
     */
    public static boolean canManage(@NonNull final CommandSender sender, @NonNull final Project project) {
        return canLead(sender, project) || (sender instanceof Player player && project.isDeputy(player.getUniqueId()));
    }

    /**
     * Like {@link #canManage}, but deputies do not qualify.
     */
    public static boolean canLead(@NonNull final CommandSender sender, @NonNull final Project project) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        return project.isLeader(player.getUniqueId()) || player.hasPermission(CommandPermissions.BYPASS);
    }

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

    public static String shortenDescription(@NonNull final Job job) {
        return StringUtils.abbreviate(job.getDescription(), 100);
    }

    public static String getPlayerHolderText(@Nullable final UUID uuid) {
        if (uuid == null) return "None";
        final String cached = EpicJobs.get().getPlayerCache().getName(uuid);
        if (cached != null) return cached;
        final String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name != null ? name : "Unknown";
    }

    public static Component mini(final String miniMessage) {
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }

    public static ItemStack getSkull(final String base64, final String name) {
        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.displayName(MiniMessage.miniMessage().deserialize(name, Palette.Role.INFO.tags()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        final PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
        playerProfile.getProperties().add(new ProfileProperty("textures", base64));
        skullMeta.setPlayerProfile(playerProfile);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

}
