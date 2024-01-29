package com.epicquestz.epicjobs.command.commands.project;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_PROJECT_COMPLETION;
import static com.epicquestz.epicjobs.constants.Messages.CANT_CREATE_PROJECT;
import static com.epicquestz.epicjobs.constants.Messages.CREATING_PROJECT;
import static com.epicquestz.epicjobs.constants.Messages.NO_PROJECTS_AVAILABLE;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_ALREADY_COMPLETE;
import static com.epicquestz.epicjobs.constants.Messages.SUCCESSFULLY_CREATED_PROJECT;

public class ProjectCommand {

	private final EpicJobs plugin;

	public ProjectCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List projects")
	@Permission(CommandPermissions.LIST_PROJECTS)
	@Command("project|projects list|ls")
	public void onList(final @NonNull CommandSender sender) {
		final List<Project> projects = plugin.getProjectManager().getProjects().stream()
            .filter(project -> project.getProjectStatus().equals(ProjectStatus.ACTIVE))
            .toList();
		sendProjectList(sender, projects);
	}

	@CommandDescription("List all projects")
	@Permission(CommandPermissions.LIST_ALL_PROJECTS)
	@Command("project|projects list|ls all")
	public void onListAll(final @NonNull CommandSender sender) {
		final List<Project> projects = plugin.getProjectManager().getProjects();
		sendProjectList(sender, projects);
	}

	private static void sendProjectList(@NotNull CommandSender sender, List<Project> projects) {
		if (projects.isEmpty()) {
			NO_PROJECTS_AVAILABLE.send(sender);
			return;
		}

		final List<TextComponent> textComponents = new ArrayList<>();
		projects.forEach(project -> {
			final TextComponent textComponent = Component.text(project.getName(), NamedTextColor.AQUA)
					.hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
					.clickEvent(ClickEvent.runCommand("/project teleport " + project.getName()));
			textComponents.add(textComponent);
		});
		final JoinConfiguration joinConfiguration = JoinConfiguration.builder()
				.separator(Component.text(", ", NamedTextColor.GOLD))
				.lastSeparator(Component.text(" and ", NamedTextColor.GOLD))
				.build();
		final Component message = Component.join(joinConfiguration, textComponents);
		sender.sendMessage("");
		sender.sendMessage(message);
		sender.sendMessage("");
	}

	@CommandDescription("Create a project")
	@Permission(CommandPermissions.CREATE_PROJECT)
	@Command("project|projects create <name> [leader]")
	public void onCreate(final @NonNull Player player,
						 @Argument(value = "name", description = "Name") final @NonNull String name,
						 @Argument(value = "leader", description = "Leader") final @Nullable Player leader
	) {
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
                final Project project = (leader == null) ? plugin.getStorage().createAndLoadProject(name, player.getUniqueId(), player.getLocation(), ProjectStatus.ACTIVE) : plugin.getStorage().createAndLoadProject(name, leader.getUniqueId(), leader.getLocation(), ProjectStatus.ACTIVE);
                plugin.getProjectManager().addProject(project);
                return project;
            })
            .syncLast((project) -> {
                final String message = (project == null) ? "§cError while creating project. Please contact an administrator." : SUCCESSFULLY_CREATED_PROJECT.toString(project.getId());
                player.sendMessage(message);
            })
            .execute();
	}

	@CommandDescription("Teleport to a project")
	@Permission(CommandPermissions.TELEPORT_PROJECT)
	@Command("project|projects teleport|tp <project>")
	public void onTeleport(final @NonNull Player player,
						   @Argument(value = "project", description = "Project") final @NonNull Project project
	) {
		project.teleport(player);
	}

	@CommandDescription("Pause a project")
	@Permission(CommandPermissions.PAUSE_PROJECT)
	@Command("project|projects pause <project>")
	public void onPause(final @NonNull CommandSender sender,
						@Argument(value = "project", description = "Project", suggestions = "active-project") final @NonNull Project project
	) {
		EpicJobs.newSharedChain("EpicJobs")
			.syncFirst(() -> {
				if (!project.getProjectStatus().equals(ProjectStatus.PAUSED)) {
					project.setProjectStatus(ProjectStatus.PAUSED);
					sender.sendMessage("Paused project " + project.getName());
					return true;
				} else {
					sender.sendMessage("Project is already paused.");
					return false;
				}
			})
			.abortIf(false)
			.async(() -> plugin.getStorage().updateProject(project))
			.execute();
	}

	@CommandDescription("Resume a project")
	@Permission(CommandPermissions.RESUME_PROJECT)
	@Command("project|projects resume|unpause <project>")
	public void onResume(final @NonNull CommandSender sender,
						 @Argument(value = "project", description = "Project", suggestions = "paused-project") final @NonNull Project project
	) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (project.getProjectStatus().equals(ProjectStatus.PAUSED)) {
                    project.setProjectStatus(ProjectStatus.ACTIVE);
                    sender.sendMessage("Unpaused project " + project.getName());
                    return true;
                } else {
					sender.sendMessage("Project is not paused.");
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorage().updateProject(project))
            .execute();
	}

	@CommandDescription("Complete a project")
	@Permission(CommandPermissions.COMPLETE_PROJECT)
	@Command("project|projects complete <project>")
	public void onComplete(final @NonNull Player player,
						   @Argument(value = "project", description = "Project", suggestions = "active-project") final @NonNull Project project
	) {
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
			.async(() -> plugin.getStorage().updateProject(project))
			.execute();
	}

}
