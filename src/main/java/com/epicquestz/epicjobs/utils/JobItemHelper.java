package com.epicquestz.epicjobs.utils;

import com.epicquestz.epicjobs.job.Job;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class JobItemHelper {

    public enum InfoType {

        PROJECT, CATEGORY, STATUS, DESCRIPTION, CREATOR, CLAIMANT;
    }

    public static ItemStack getJobItem(final Job job, final String actionMessage, final InfoType... infoList) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(job.getJobCategory().getMaterial())
            .withName("§f§lJob " + job.getId());
        final String[] actionMessageLines = actionMessage.split("\n");
        for (final String line : actionMessageLines) {
            itemStackBuilder.withLore(line);
        }
        for (final InfoType informationType : infoList) {
            switch (informationType) {
                case PROJECT:
                    itemStackBuilder.withLore("§f§lProject: §f" + job.getProject().getName());
                    break;
                case CATEGORY:
                    itemStackBuilder.withLore("§f§lCategory: §f" + job.getJobCategory().getName());
                    break;
                case STATUS:
                    itemStackBuilder.withLore("§f§lStatus: §f" + job.getJobStatus().name());
                    break;
                case DESCRIPTION:
                    itemStackBuilder.withLineBreakLore(ChatColor.GRAY, job.getDescription());
                    break;
                case CREATOR:
                    itemStackBuilder.withLore("§f§lCreator: §f" + Utils.getPlayerHolderText(job.getCreator()));
                    break;
                case CLAIMANT:
                    itemStackBuilder.withLore("§f§lClaimant: §f" + Utils.getPlayerHolderText(job.getClaimant()));
                    break;
            }
        }

        return itemStackBuilder.build();
    }
}
