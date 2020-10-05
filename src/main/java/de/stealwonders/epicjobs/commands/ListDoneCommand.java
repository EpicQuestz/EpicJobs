package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.utils.ItemStackBuilder;
import de.stealwonders.epicjobs.utils.JobItemHelper;
import de.stealwonders.epicjobs.utils.MenuHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.stealwonders.epicjobs.model.job.JobStatus.DONE;

@CommandAlias("job|jobs")
@CommandPermission("epicjobs.command.job")
public class ListDoneCommand extends BaseCommand {

    private final EpicJobs plugin;

    public ListDoneCommand(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Subcommand("list done")
    @CommandPermission("epicjobs.command.job.list.done")
    public void onListDone(final Player player) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(DONE))
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

        final Gui gui = MenuHelper.getPaginatedGui(title, guiItems, null, infoBook);
        gui.show(player);
    }
}
