package com.epicquestz.epicjobs.utils;

import com.epicquestz.epicjobs.constants.Palette;
import com.epicquestz.epicjobs.job.Job;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;

public class JobItemHelper {

    public enum InfoType {

        PROJECT, CATEGORY, STATUS, DESCRIPTION, CREATOR, CLAIMANT;
    }

    /**
     * Builds a job tooltip mirroring the {@code /job info} chat layout: bold white title, muted
     * labels with slate values, the description quoted in italics, and the action hints last —
     * each group separated by a blank line. The description is always rendered after the other
     * fields regardless of its position in {@code infoList}.
     */
    public static ItemStack getJobItem(final Job job, final String actionMessage, final InfoType... infoList) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(job.getJobCategory().getMaterial())
            .withName(Component.text("Job #" + job.getId(), Palette.Role.INFO.accent(), TextDecoration.BOLD));
        String description = null;
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
                    description = job.getDescription();
                    break;
                case CREATOR:
                    itemStackBuilder.withLore(info("Creator: ", Utils.getPlayerHolderText(job.getCreator())));
                    break;
                case CLAIMANT:
                    itemStackBuilder.withLore(info("Claimant: ", Utils.getPlayerHolderText(job.getClaimant())));
                    break;
            }
        }
        if (description != null) {
            itemStackBuilder.withLore(Component.empty());
            itemStackBuilder.withLineBreakLore(Palette.Role.INFO.body(), "\"" + description + "\"", true);
        }
        itemStackBuilder.withLore(Component.empty());
        for (final String line : actionMessage.split("\n")) {
            itemStackBuilder.withLore(line);
        }

        return itemStackBuilder.build();
    }

    // Muted label followed by a slate value. Built as components so dynamic values
    // (project / player names) are never interpreted as MiniMessage.
    private static Component info(final String label, final String value) {
        return Component.text()
            .append(Component.text(label, Palette.MUTED))
            .append(Component.text(value, Palette.Role.INFO.body()))
            .build();
    }
}
