package de.stealwonders.epicjobs.command.commands.project;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.project.Project;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TeleportProjectCommand {

	@CommandDescription("Teleport to a project site")
	@CommandMethod("project|projects teleport|tp <project>")
	public void onCommand(final @NonNull Player player,
						  @Argument(value = "project", description = "Project") final @NonNull Project project) {
		player.teleportAsync(project.getLocation());
		player.sendMessage(Component.text("Teleported to project:").color(Colors.HONEY_YELLOW)
				.append(Component.space())
				.append(Component.text(project.getName()).color(Colors.CARIBBEAN_GREEN)));
	}

}