package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.constants.SkullHeads;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobStatus;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.utils.ItemStackBuilder;
import de.stealwonders.epicjobs.utils.JobItemHelper;
import de.stealwonders.epicjobs.utils.MenuHelper;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("job|jobs")
@CommandPermission("epicjobs.command.job")
public class ListAllCommand extends BaseCommand {

    private final EpicJobs plugin;

    private static final ItemStack BACK_BUTTON = Utils.getSkull(SkullHeads.OAK_WOOD_ARROW_LEFT.getBase64(), "§f§lBack");

    public ListAllCommand(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Subcommand("list all")
    public void onListAll(final Player player) {
        sendSelectionMenu(player);
    }

    private void sendSelectionMenu(final Player player) {
        final GuiItem projectItem = new GuiItem(new ItemStackBuilder(Material.SCAFFOLDING).withName("§f§lProjects").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendProjectMenu(player);
        });

        final GuiItem statusItem = new GuiItem(new ItemStackBuilder(Material.OAK_SIGN).withName("§f§lStatus").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendStatusMenu(player);
        });

        final GuiItem categoryItem = new GuiItem(new ItemStackBuilder(Material.JUKEBOX).withName("§f§lCategory").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            sendCategoryMenu(player);
        });

        final Gui gui = MenuHelper.getStaticSelectionGui("Select Filter", projectItem, statusItem, categoryItem);
        gui.show(player);
    }

    private void sendProjectMenu(final Player player) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Project project : plugin.getProjectManager().getProjects()) {
            final ItemStack itemStack = new ItemStackBuilder(Material.SCAFFOLDING)
                    .withName("§f§l" + project.getName())
                    .withLore("§7Shift-click to teleport")
                    .withLore("§f§lLeader: §f" + Utils.getPlayerHolderText(project.getLeader()))
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
        final Gui gui = MenuHelper.getPaginatedSelectionGui("Current Projects", guiItems);
        gui.show(player);
    }

    private void sendStatusMenu(final Player player) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final JobStatus jobStatus : JobStatus.values()) {
            final GuiItem guiItem = new GuiItem(new ItemStackBuilder(Material.OAK_SIGN).withName("§f§l" + jobStatus.name()).build(), inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                final List<Job> jobs = plugin.getJobManager().getJobs().stream()
                        .filter(job -> job.getJobStatus().equals(jobStatus))
                        .collect(Collectors.toList());
                sendJobMenu(player, jobs);
            });
            guiItems.add(guiItem);
        }
        final Gui gui = MenuHelper.getStaticSelectionGui("Select Job Status", guiItems.toArray(new GuiItem[guiItems.size()]));
        gui.show(player);
    }

    private void sendCategoryMenu(final Player player) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final JobCategory jobCategory : JobCategory.values()) {
            final GuiItem guiItem = new GuiItem(new ItemStackBuilder(jobCategory.getMaterial()).withName("§f§l" + jobCategory.name()).build(), inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                final List<Job> jobs = plugin.getJobManager().getJobs().stream()
                        .filter(job -> job.getJobCategory().equals(jobCategory))
                        .collect(Collectors.toList());
                sendJobMenu(player, jobs);
            });
            guiItems.add(guiItem);
        }
        final Gui gui = MenuHelper.getStaticSelectionGui("Select Job Category", guiItems.toArray(new GuiItem[guiItems.size()]));
        gui.show(player);
    }

    private void sendJobMenu(final Player player, final List<Job> jobs) {
        final List<GuiItem> guiItems = new ArrayList<>();
        for (final Job job : jobs) {
            final ItemStack itemStack = JobItemHelper.getJobItem(job, "§7Shift-click to §lteleport", JobItemHelper.InfoType.PROJECT, JobItemHelper.InfoType.CATEGORY, JobItemHelper.InfoType.STATUS, JobItemHelper.InfoType.DESCRIPTION, JobItemHelper.InfoType.CREATOR, JobItemHelper.InfoType.CLAIMANT);
            final GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setResult(Event.Result.DENY);
                switch (inventoryClickEvent.getClick()) {
                    case SHIFT_LEFT:
                    case SHIFT_RIGHT:
                        Bukkit.dispatchCommand(player, "job teleport " + job.getId());
                        break;
                    case LEFT:
                    case RIGHT:
                        player.sendMessage(job.getDescription());
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
                .withName("§f§lInformation")
                .withLore("§7§lTeleport §7to job by using shift-click")
                .withLore("§7Click to §lview job info")
                .build();

        final Gui gui = MenuHelper.getPaginatedGui("Available Jobs", guiItems, mainMenuItem, infoBook);
        gui.show(player);
    }

}
