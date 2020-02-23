package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.stealwonders.epicjobs.constants.Messages.*;

@CommandAlias("project|projects")
public class ProjectCommand extends BaseCommand {

    private EpicJobs plugin;

    public ProjectCommand(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(final CommandSender commandSender) {
        plugin.getProjectManager().getProjects().forEach(project -> commandSender.sendMessage("#" + project.getId() + " " +  project.getName()));
    }

    @Subcommand("list")
    public void onList(final CommandSender sender) {
        final List<Project> projects = plugin.getProjectManager().getProjects().stream()
                .filter(project -> project.getProjectStatus() == ProjectStatus.ACTIVE)
                .collect(Collectors.toList());
        if (projects.size() >= 1) {
            projects.forEach(project -> sender.sendMessage("#" + project.getId() + " | " + project.getName()));
        } else {
            NO_PROJECTS_AVAILABLE.send(sender);
        }
    }

    @Subcommand("create")
    @CommandPermission("epicjobs.command.project.create")
    public void onCreate(final Player player, @Single final String name, @Optional final Player leader) {
        int id = plugin.getProjectManager().getFreeId();
        if (plugin.getProjectManager().getProjectByName(name) == null) {
            Project project = (leader == null) ? new Project(id, name, player) : new Project(id, name, leader);
            plugin.getProjectManager().addProject(project);
            player.sendMessage("Successfully created project with id #" + id);
        } else {
            player.sendMessage("Cannot create a project with duplicate name.");
        }
    }

    @Subcommand("edit")
    @CommandCompletion("@project *")
    @CommandPermission("epicjobs.command.project.edit")
    public void onEdit(final Player player, final Project project, final String context, final @Optional String option) {
        switch (context.toUpperCase()) {
            case "NAME":
                project.setName(option);
                player.sendMessage("Set name of project to " + option);
                break;
            case "LOCATION":
                project.setLocation(player.getLocation());
                player.sendMessage("The project site has been set to your position");
                break;
            case "LEADER":
                Player leader = Bukkit.getPlayer(option);
                if (leader != null) {
                    project.setLeader(leader);
                    player.sendMessage("Project leader has been set to " + leader.getName());
                } else {
                    PLAYER_NOT_FOUND.send(player, option);
                }
                break;
            default:
                throw new InvalidCommandArgument();
        }
    }

    @Subcommand("teleport|tp")
    @CommandCompletion("@project")
    public void onTeleport(final Player player, final Project project) {
        project.teleport(player);
    }

    @Subcommand("complete")
    @CommandCompletion("@active-project")
    @CommandPermission("epicjobs.command.project.complete")
    public void onComplete(final Player player, final Project project) {
        if (project.getProjectStatus() != ProjectStatus.COMPLETE) {
            project.setProjectStatus(ProjectStatus.COMPLETE);
            ANNOUNCE_PROJECT_COMPLETION.broadcast(project.getName());
        } else {
            player.sendMessage("This project is already marked as complete.");
        }
    }
}
