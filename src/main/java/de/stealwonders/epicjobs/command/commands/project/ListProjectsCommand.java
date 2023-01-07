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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public record ListProjectsCommand(EpicJobs plugin) {

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
			commandSender.sendMessage(Component.text().content("There are no projects to show.").color(NamedTextColor.RED).build());
			return;
		}

		List<Component> components = new ArrayList<>();
		projects.forEach(project -> {
			Component component = MiniMessage.miniMessage().deserialize("<aqua><hover:show_text:\"Click to teleport!\"><click:run_command:/project teleport<project>><project></click></hover></aqua>", Placeholder.unparsed("project", project.getName()));
			if (verbose) {
				component = component.append(Component.space());
				component = component.append(MiniMessage.miniMessage().deserialize("<gold>(<projectstatus>)</gold>", Placeholder.unparsed("projectstatus", project.getProjectStatus().name())));
			}
			components.add(component);
		});

		Component message = Component.join(JoinConfiguration.separator(Component.text(", ").color(Colors.HONEY_YELLOW)), components);

		commandSender.sendMessage("");
		commandSender.sendMessage(message);
		commandSender.sendMessage("");
	}

}