package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.utils.ItemStackBuilder;
import com.epicquestz.epicjobs.utils.JobItemHelper;
import com.epicquestz.epicjobs.utils.MenuHelper;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobListDoneCommand {

	private final EpicJobs plugin;

	public JobListDoneCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List done jobs")
//	@Permission() // todo: command.job
	@Command("job|jobs list|ls done")
	public void onListDone(final Player player) {
		final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(JobStatus.DONE))
            .collect(Collectors.toList());
        sendJobMenu(player, "Available Jobs", jobs);
	}


	private void sendJobMenu(final Player player, final String title, final List<Job> jobs) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Job job : jobs) {
            final ItemStack itemStack = JobItemHelper.getJobItem(job, "§7Shift left-click to mark §lcomplete", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR, JobItemHelper.InfoType.CLAIMANT);
            final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                switch (inventoryClickEvent.getClick()) {
                    case SHIFT_LEFT:
                        Bukkit.dispatchCommand(player, "job complete " + job.getId());
                        break;
                    case SHIFT_RIGHT:
                        Bukkit.dispatchCommand(player, "job reopen " + job.getId());
                        break;
                    case LEFT:
                        job.teleport(player);
                        break;
                    case RIGHT:
                        player.sendMessage(job.getDescription());
                        break;
                }
            });
            guiItems.add(guiItem);
        }

        final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
            .withName("§f§lInformation")
            .withLore("§7Mark job as §lcomplete §7by using shift left-click")
            .withLore("§7§lReopen §7job by using shift right-click")
            .withLore("§7Click to §lview job info")
            .build();

        final ChestGui gui = MenuHelper.getPaginatedGui(title, guiItems, null, infoBook);
        gui.show(player);
    }

}
