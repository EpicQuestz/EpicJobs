package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.SkullHeads;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.utils.ItemStackBuilder;
import com.epicquestz.epicjobs.utils.JobItemHelper;
import com.epicquestz.epicjobs.utils.MenuHelper;
import com.epicquestz.epicjobs.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobListAllCommand {

	private static final ItemStack BACK_BUTTON = Utils.getSkull(SkullHeads.OAK_WOOD_ARROW_LEFT.getBase64(), "<white><bold>Back");

	private final EpicJobs plugin;

	public JobListAllCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List all jobs")
    @Permission(CommandPermissions.LIST_ALL_JOBS)
	@Command("job|jobs list|ls all")
	public void onListAll(final Player player) {
		sendSelectionMenu(player);
	}

	private void sendSelectionMenu(final Player player) {
        final GuiItem projectItem = new GuiItem(new ItemStackBuilder(Material.SCAFFOLDING).withName("<white><bold>Projects").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendProjectMenu(player);
        });

        final GuiItem statusItem = new GuiItem(new ItemStackBuilder(Material.OAK_SIGN).withName("<white><bold>Status").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendStatusMenu(player);
        });

        final GuiItem categoryItem = new GuiItem(new ItemStackBuilder(Material.JUKEBOX).withName("<white><bold>Category").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendCategoryMenu(player);
        });

        final ChestGui gui = MenuHelper.getStaticSelectionGui("Select Filter", projectItem, statusItem, categoryItem);
        gui.show(player);
    }

    private void sendProjectMenu(final Player player) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Project project : plugin.getProjectManager().getProjects()) {
            final ItemStack itemStack = new ItemStackBuilder(Material.SCAFFOLDING)
				.withName(Component.text(project.getName(), NamedTextColor.WHITE, TextDecoration.BOLD))
				.withLore("<gray>Shift-click to teleport")
				.withLore("<white><bold>Leader: </bold>" + Utils.getPlayerHolderText(project.getLeader()))
				.build();
            final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                switch (inventoryClickEvent.getClick()) {
                    case SHIFT_LEFT:
                    case SHIFT_RIGHT:
                        project.teleport(player);
                        break;
                    case LEFT:
                    case RIGHT:
                        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
                                .filter(job -> job.getProject().equals(project))
                                .collect(Collectors.toList());
                        sendJobMenu(player, jobs);
                        break;
                }
            });
            guiItems.add(guiItem);
        }
        final ChestGui gui = MenuHelper.getPaginatedSelectionGui("Current Projects", guiItems);
        gui.show(player);
    }

    private void sendStatusMenu(final Player player) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final JobStatus jobStatus : JobStatus.values()) {
            final GuiItem guiItem = new GuiItem(new ItemStackBuilder(Material.OAK_SIGN).withName("<white><bold>" + jobStatus.name()).build(), inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                final List<Job> jobs = plugin.getJobManager().getJobs().stream()
                        .filter(job -> job.getJobStatus().equals(jobStatus))
                        .collect(Collectors.toList());
                sendJobMenu(player, jobs);
            });
            guiItems.add(guiItem);
        }
        final ChestGui gui = MenuHelper.getStaticSelectionGui("Select Job Status", guiItems.toArray(new GuiItem[guiItems.size()]));
        gui.show(player);
    }

    private void sendCategoryMenu(final Player player) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final JobCategory jobCategory : JobCategory.values()) {
            final GuiItem guiItem = new GuiItem(new ItemStackBuilder(jobCategory.getMaterial()).withName("<white><bold>" + jobCategory.name()).build(), inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                final List<Job> jobs = plugin.getJobManager().getJobs().stream()
                        .filter(job -> job.getJobCategory().equals(jobCategory))
                        .collect(Collectors.toList());
                sendJobMenu(player, jobs);
            });
            guiItems.add(guiItem);
        }
        final ChestGui gui = MenuHelper.getStaticSelectionGui("Select Job Category", guiItems.toArray(new GuiItem[guiItems.size()]));
        gui.show(player);
    }

    private void sendJobMenu(final Player player, final List<Job> jobs) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Job job : jobs) {
            final ItemStack itemStack = JobItemHelper.getJobItem(job, "<gray>Shift-click to <bold>teleport", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR, JobItemHelper.InfoType.CLAIMANT);
            final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                switch (inventoryClickEvent.getClick()) {
                    case SHIFT_LEFT:
                    case SHIFT_RIGHT:
                        Bukkit.dispatchCommand(player, "job teleport " + job.getId());
                        break;
                    case LEFT:
                    case RIGHT:
                        player.sendMessage(Component.text(job.getDescription()));
                        break;
                }
            });
            guiItems.add(guiItem);
        }

        final GuiItem mainMenuItem = new GuiItem(BACK_BUTTON, inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendSelectionMenu(player);
        });

        final ItemStack infoBook = new ItemStackBuilder(Material.BOOK)
                .withName("<white><bold>Information")
                .withLore("<gray><bold>Teleport</bold> to job by using shift-click")
                .withLore("<gray>Click to <bold>view job info")
                .build();

        final ChestGui gui = MenuHelper.getPaginatedGui("All Jobs", guiItems, mainMenuItem, infoBook);
        gui.show(player);
    }

}
