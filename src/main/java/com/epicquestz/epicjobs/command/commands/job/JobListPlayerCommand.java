package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.SkullHeads;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.utils.ItemStackBuilder;
import com.epicquestz.epicjobs.utils.JobItemHelper;
import com.epicquestz.epicjobs.utils.MenuHelper;
import com.epicquestz.epicjobs.utils.Utils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.List;

import static com.epicquestz.epicjobs.constants.Messages.TARGET_HAS_NO_JOBS;

public class JobListPlayerCommand {

	private static final ItemStack BACK_BUTTON = Utils.getSkull(SkullHeads.OAK_WOOD_ARROW_LEFT.getBase64(), "<em>Back");

	public enum StatusFilter {

		ACTIVE(JobStatus.TAKEN, "Active", Material.WRITABLE_BOOK),
		DONE(JobStatus.DONE, "Done", Material.WRITTEN_BOOK),
		COMPLETE(JobStatus.COMPLETE, "Completed", Material.BOOKSHELF);

		private final JobStatus jobStatus;
		private final String displayName;
		private final Material material;

		StatusFilter(final JobStatus jobStatus, final String displayName, final Material material) {
			this.jobStatus = jobStatus;
			this.displayName = displayName;
			this.material = material;
		}
	}

	private final EpicJobs plugin;

	public JobListPlayerCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List jobs claimed by a player")
	@Permission(CommandPermissions.LIST_PLAYER_JOBS)
	@Command("job|jobs list|ls player <player> [status]")
	public void onListPlayer(final @NonNull Player player,
							 @Argument(value = "player", description = "Player") final @NonNull OfflinePlayer target,
							 @Argument(value = "status", description = "Status") final @Nullable StatusFilter status) {
		// Filter the global job list by claimant instead of going through the User
		// profile, so this also works for players who are currently offline.
		final List<Job> jobs = plugin.getJobManager().getJobs().stream()
			.filter(job -> target.getUniqueId().equals(job.getClaimant()))
			.toList();
		if (jobs.isEmpty()) {
			TARGET_HAS_NO_JOBS.send(player, Utils.getPlayerHolderText(target.getUniqueId()));
			return;
		}
		if (status == null) {
			sendStatusSelectionMenu(player, target, jobs);
		} else {
			sendJobMenu(player, target, jobs, status);
		}
	}

	private void sendStatusSelectionMenu(final Player player, final OfflinePlayer target, final List<Job> jobs) {
		final String targetName = Utils.getPlayerHolderText(target.getUniqueId());

		final List<GuiItem> guiItems = new ArrayList<>();
		for (final StatusFilter filter : StatusFilter.values()) {
			final ItemStack itemStack = new ItemStackBuilder(filter.material).withName("<em>" + filter.displayName + " Jobs").build();
			// suppress the built-in "Original" generation line on the written book
			itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
				.addHiddenComponents(DataComponentTypes.WRITTEN_BOOK_CONTENT)
				.build());
			final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
				inventoryClickEvent.setResult(Event.Result.DENY);
				sendJobMenu(player, target, jobs, filter);
			});
			guiItems.add(guiItem);
		}

		final ChestGui gui = MenuHelper.getStaticSelectionGui(targetName + "'s Jobs", null, guiItems.toArray(new GuiItem[0]));
		gui.show(player);
	}

	private void sendJobMenu(final Player player, final OfflinePlayer target, final List<Job> jobs, final StatusFilter filter) {
		final GuiItem mainMenuItem = new GuiItem(BACK_BUTTON, inventoryClickEvent -> {
			inventoryClickEvent.setResult(Event.Result.DENY);
			sendStatusSelectionMenu(player, target, jobs);
		});

		final List<GuiItem> guiItems = new ArrayList<>();
		for (final Job job : jobs) {
			if (!job.getJobStatus().equals(filter.jobStatus)) continue;
			final ItemStack itemStack = JobItemHelper.getJobItem(job, "<muted>Click to <em>teleport</em>", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR);
			final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
				inventoryClickEvent.setResult(Event.Result.DENY);
				job.teleport(player);
			});
			guiItems.add(guiItem);
		}

		final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
			.withName("<em>Information")
			.withLore("<muted>Click to <em>teleport</em> to a job")
			.build();

		final String title = Utils.getPlayerHolderText(target.getUniqueId()) + "'s " + filter.displayName + " Jobs";
		final ChestGui gui = MenuHelper.getPaginatedGui(title, guiItems, mainMenuItem, infoBook);
		gui.show(player);
	}

}
