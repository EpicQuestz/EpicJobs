package de.stealwonders.epicjobs.command.commands.project;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.model.project.ProjectStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public record ListProjectsCommand(EpicJobs plugin) {

	private static final Component NO_PROJECTS = Component.text("There are no jobs to show.").color(NamedTextColor.RED);

	@CommandDescription("List all active projects")
	@CommandMethod("project|projects list")
	public void onList(final @NonNull CommandSender commandSender) {
		final List<Project> projects = plugin.getStorage().getProjects().stream()
				.filter(project -> project.getProjectStatus() == ProjectStatus.ACTIVE).toList();
		printProjects(commandSender, projects, false);
	}

	@CommandDescription("List all projects regardless of its status")
	@CommandPermission(CommandPermissions.LIST_ALL_PROJECTS)
	@CommandMethod("project|projects list all")
	public void onListAll(final @NonNull CommandSender commandSender) {
		final List<Project> projects = plugin.getStorage().getProjects();
		printProjects(commandSender, projects, true);
	}

	private void printProjects(CommandSender commandSender, List<Project> projects, boolean verbose) {
		if (projects.size() < 1) {
			commandSender.sendMessage(NO_PROJECTS);
			return;
		}

		final List<Component> components = new ArrayList<>();
		projects.forEach(project -> {
			Component component = Component.text(project.getName()).color(Colors.CARIBBEAN_GREEN)
					.hoverEvent(Component.text("Click to teleport!").color(Colors.CULTURED_WHITE))
					.clickEvent(ClickEvent.runCommand("/project teleport " + project.getTag()));
			if (verbose) {
				component = component.append(Component.space());
				component = component.append(Component.text("(" + project.getProjectStatus().name() + ")").color(Colors.HONEY_YELLOW));
			}
			components.add(component);
		});

		final Component message = Component.join(JoinConfiguration.separator(Component.text(", ").color(Colors.HONEY_YELLOW)), components);

		commandSender.sendMessage("");
		commandSender.sendMessage(message);
		commandSender.sendMessage("");
	}

}