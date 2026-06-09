package com.epicquestz.epicjobs.utils;

import com.epicquestz.epicjobs.job.Job;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;

public class JobItemHelper {

    public enum InfoType {

        PROJECT, CATEGORY, STATUS, DESCRIPTION, CREATOR, CLAIMANT;
    }

    public static ItemStack getJobItem(final Job job, final String actionMessage, final InfoType... infoList) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(job.getJobCategory().getMaterial())
            .withName("<white><bold>Job " + job.getId());
        final String[] actionMessageLines = actionMessage.split("\n");
        for (final String line : actionMessageLines) {
            itemStackBuilder.withLore(line);
        }
        for (final InfoType informationType : infoList) {
            switch (informationType) {
                case PROJECT:
                    itemStackBuilder.withLore(info("Project: ", job.getProject().getName()));
                    break;
                case CATEGORY:
                    itemStackBuilder.withLore(info("Category: ", job.getJobCategory().getName()));
                    break;
                case STATUS:
                    itemStackBuilder.withLore(info("Status: ", job.getJobStatus().name()));
                    break;
                case DESCRIPTION:
                    itemStackBuilder.withLineBreakLore(NamedTextColor.GRAY, job.getDescription());
                    break;
                case CREATOR:
                    itemStackBuilder.withLore(info("Creator: ", Utils.getPlayerHolderText(job.getCreator())));
                    break;
                case CLAIMANT:
                    itemStackBuilder.withLore(info("Claimant: ", Utils.getPlayerHolderText(job.getClaimant())));
                    break;
            }
        }

        return itemStackBuilder.build();
    }

    // Bold white label followed by a plain white value. Built as components so dynamic values
    // (project / player names) are never interpreted as MiniMessage.
    private static Component info(final String label, final String value) {
        return Component.text()
            .append(Component.text(label, NamedTextColor.WHITE, TextDecoration.BOLD))
            .append(Component.text(value, NamedTextColor.WHITE))
            .build();
    }
}
