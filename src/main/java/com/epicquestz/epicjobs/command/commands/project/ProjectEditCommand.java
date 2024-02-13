package com.epicquestz.epicjobs.command.commands.project;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class ProjectEditCommand {

	private final EpicJobs plugin;

	public ProjectEditCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("Edit a project's name")
	@Permission(CommandPermissions.MODIFY_PROJECT_NAME)
	@Command("project|projects edit|e name <project> <name>")
	public void onEditName(@NonNull CommandSender sender,
						   @Argument(value = "project", description = "Project") final @NonNull Project project,
						   @Argument(value = "name", description = "Name") final @NonNull String name
	) {
		project.setName(name);
		sender.sendMessage("Set name of project to " + name); // todo: formatting
		plugin.getStorage().updateProject(project); // todo: async
	}

	@CommandDescription("Edit a project's leader")
	@Permission(CommandPermissions.MODIFY_PROJECT_LEADER)
	@Command("project|projects edit|e leader <project> <leader>")
	public void onEditLeader(@NonNull CommandSender sender,
							 @Argument(value = "project", description = "Project") final @NonNull Project project,
							 @Argument(value = "leader", description = "Leader", suggestions = "all-offline-players") final @NonNull OfflinePlayer leader
	) {
		project.setLeader(leader.getUniqueId());
		sender.sendMessage("Set project leader to " + leader.getName());
		plugin.getStorage().updateProject(project);
	}

	@CommandDescription("Edit a project's location")
	@Permission(CommandPermissions.MODIFY_PROJECT_LOCATION)
	@Command("project|projects edit|e location <project>")
	public void onEditLocation(@NonNull Player player,
							   @Argument(value = "project", description = "Project") final @NonNull Project project
	) {
		project.setLocation(player.getLocation());
		player.sendMessage("Updated project location to your current");
        plugin.getStorage().updateProject(project);
	}

	@CommandDescription("Edit a project's status")
	@Permission(CommandPermissions.MODIFY_PROJECT_STATUS)
	@Command("project|projects edit|e status <project> <status>")
	public void onEditStatus(@NonNull CommandSender sender,
							 @Argument(value = "project", description = "Project") final @NonNull Project project,
							 @Argument(value = "status", description = "Status") final @NonNull ProjectStatus status
	) {
		project.setProjectStatus(status);
		sender.sendMessage("Set project status to " + status.name());
		plugin.getStorage().updateProject(project);
	}

}
