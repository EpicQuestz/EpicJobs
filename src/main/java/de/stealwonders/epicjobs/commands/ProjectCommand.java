package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static de.stealwonders.epicjobs.constants.Messages.NO_PROJECTS_AVAILABLE;

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
    public void onCreate(final Player player, String[] args) {
        int id = plugin.getProjectManager().getFreeId();
        String description = String.valueOf(args);
        Project project = new Project(id, description, player);
        plugin.getProjectManager().addProject(project);
        player.sendMessage("Successfully created project with id #" + id);
    }




}
