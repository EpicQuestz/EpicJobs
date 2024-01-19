package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.stealwonders.epicjobs.constants.Messages.*;

@CommandAlias("project|projects")
@CommandPermission("epicjobs.command.project")
public class ProjectCommand extends BaseCommand {

    private final EpicJobs plugin;

    public ProjectCommand(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    public void onHelp(final CommandSender commandSender, final CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("list")
    public void onList(final CommandSender sender) {
        final List<Project> projects = plugin.getProjectManager().getProjects().stream()
            .filter(project -> project.getProjectStatus().equals(ProjectStatus.ACTIVE))
            .toList();
        if (!projects.isEmpty()) {
            final List<Component> textComponents = new ArrayList<>();
            projects.forEach(project -> {
                final Component textComponent = Component.text(project.getName()).color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                    .clickEvent(ClickEvent.runCommand("/project teleport " + project.getName()));
                textComponents.add(textComponent);
            });
            final JoinConfiguration joinConfiguration = JoinConfiguration.builder()
                .separator(Component.text(", ").color(NamedTextColor.GOLD))
                .lastSeparator(Component.text(" and ").color(NamedTextColor.GOLD))
                .build();
            final Component message = Component.join(joinConfiguration, textComponents);
            sender.sendMessage("");
            sender.sendMessage(message);
            sender.sendMessage("");
        } else {
            NO_PROJECTS_AVAILABLE.send(sender);
        }
    }

    @Subcommand("list all")
    @CommandPermission("epicjobs.command.project.listall")
    public void onListAll(final CommandSender sender) {
        final List<Project> projects = plugin.getProjectManager().getProjects();
        if (!projects.isEmpty()) {
            final List<Component> textComponents = new ArrayList<>();
            projects.forEach(project -> {
                final Component textComponent = Component.text()
                    .append(Component.text(project.getName()).color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                    .clickEvent(ClickEvent.runCommand("/project teleport " + project.getName())))
                    .append(Component.text(" (" + project.getProjectStatus() + ")").color(NamedTextColor.GOLD))
                    .build();
                textComponents.add(textComponent);
            });
            final JoinConfiguration joinConfiguration = JoinConfiguration.builder()
                    .separator(Component.text(", ").color(NamedTextColor.GOLD))
                    .lastSeparator(Component.text(" and ").color(NamedTextColor.GOLD))
                    .build();
            final Component message = Component.join(joinConfiguration, textComponents);
            sender.sendMessage("");
            sender.sendMessage(message);
            sender.sendMessage("");
        } else {
            NO_PROJECTS_AVAILABLE.send(sender);
        }
    }

    @Subcommand("create")
    @CommandPermission("epicjobs.command.project.create")
    public void onCreate(final Player player, @Single final String name, @Optional final Player leader) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (plugin.getProjectManager().getProjectByName(name) == null) {
                    CREATING_PROJECT.sendActionbar(player, name);
                    return true;
                } else {
                    CANT_CREATE_PROJECT.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .asyncFirst(() -> {
                final Project project = (leader == null) ? plugin.getStorageImplementation().createAndLoadProject(name, player.getUniqueId(), player.getLocation(), ProjectStatus.ACTIVE) : plugin.getStorageImplementation().createAndLoadProject(name, leader.getUniqueId(), leader.getLocation(), ProjectStatus.ACTIVE);
                plugin.getProjectManager().addProject(project);
                return project;
            })
            .syncLast((project) -> {
                final String message = (project == null) ? "§cError while creating project. Please contact an administrator." : SUCCESSFULLY_CREATED_PROJECT.toString(project.getId());
                player.sendMessage(message);
            })
            .execute();
    }

    @Subcommand("edit name")
    @CommandCompletion("@project @nothing")
    @CommandPermission("epicjobs.command.project.edit")
    public void onEditName(final Player player, final Project project, final String name) {
        project.setName(name);
        player.sendMessage("Set name of project to " + name);
        plugin.getStorageImplementation().updateProject(project);
    }

    @Subcommand("edit location")
    @CommandCompletion("@project")
    @CommandPermission("epicjobs.command.project.edit")
    public void onEditLocation(final Player player, final Project project) {
        project.setLocation(player.getLocation());
        player.sendMessage("Updated project location to your current");
        plugin.getStorageImplementation().updateProject(project);
    }

    @Subcommand("edit leader")
    @CommandCompletion("@project @players")
    @CommandPermission("epicjobs.command.project.edit")
    public void onEditLeader(final Player player, final Project project, final OnlinePlayer leader) {
        project.setLeader(leader.getPlayer());
        player.sendMessage("Set project leader to " + leader.getPlayer().getName());
        plugin.getStorageImplementation().updateProject(project);
    }

    @Subcommand("teleport|tp")
    @CommandCompletion("@project")
    public void onTeleport(final Player player, final Project project) {
        project.teleport(player);
    }

    @Subcommand("pause")
    @CommandCompletion("@active-project")
    @CommandPermission("epicjobs.command.project.pause")
    public void onPause(final Player player, final Project project) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (!project.getProjectStatus().equals(ProjectStatus.PAUSED)) {
                    project.setProjectStatus(ProjectStatus.PAUSED);
                    player.sendMessage("Paused project " + project.getName());
                    return true;
                } else {
                    player.sendMessage("Project is already paused.");
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateProject(project))
            .execute();
    }

    @Subcommand("unpause")
    @CommandCompletion("@paused-project")
    @CommandPermission("epicjobs.command.project.unpause")
    public void onUnpause(final Player player, final Project project) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (project.getProjectStatus().equals(ProjectStatus.PAUSED)) {
                    project.setProjectStatus(ProjectStatus.ACTIVE);
                    player.sendMessage("Unpaused project " + project.getName());
                    return true;
                } else {
                    player.sendMessage("Project is not paused.");
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateProject(project))
            .execute();
    }


    @Subcommand("complete")
    @CommandCompletion("@active-project")
    @CommandPermission("epicjobs.command.project.complete")
    public void onComplete(final Player player, final Project project) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (!project.getProjectStatus().equals(ProjectStatus.COMPLETE)) {
                    project.setProjectStatus(ProjectStatus.COMPLETE);
                    ANNOUNCE_PROJECT_COMPLETION.broadcast(project.getName());
                    return true;
                } else {
                    PROJECT_ALREADY_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateProject(project))
            .execute();
    }

}
