package de.stealwonders.epicjobs.command.commands.project;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.model.project.ProjectStatus;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

@CommandPermission(CommandPermissions.MODIFY_PROJECT)
public class ProjectStatusCommands {

	@CommandDescription("Pauses a project")
	@CommandMethod("project|projects pause <project>")
	public void onPause(final @NonNull CommandSender commandSender,
						@Argument(value = "project", description = "Project") final @NonNull Project project) {
		project.setProjectStatus(ProjectStatus.PAUSED);
		// todo: update database reference and send message
	}

	@CommandDescription("Resumes a project")
	@CommandMethod("project|projects resume <project>")
	public void onResume(final @NonNull CommandSender commandSender,
						 @Argument(value = "project", description = "Project") final @NonNull Project project) {
		project.setProjectStatus(ProjectStatus.ACTIVE);
		// todo: update database reference and send message
	}

	@CommandDescription("Completes a project")
	@CommandMethod("project|projects complete <project>")
	public void onComplete(final @NonNull CommandSender commandSender,
						   @Argument(value = "project", description = "Project") final @NonNull Project project) {
		project.setProjectStatus(ProjectStatus.COMPLETE);
		// todo: update database reference and send message
	}

}