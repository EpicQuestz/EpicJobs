package de.stealwonders.epicjobs.command.commands.project;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.model.project.ProjectStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@CommandPermission(CommandPermissions.MODIFY_PROJECT)
public record EditProjectCommand(EpicJobs plugin) {

//	CompletableFuture<Optional<Project>> promise = CompletableFuture.supplyAsync(() -> Optional.of(project));
//		promise.thenAcceptAsync(optional -> {
//		if (optional.isEmpty()) {
//			commandSender.sendMessage("§cAn error occurred while updating the project.");
//			return;
//		}
//		commandSender.sendMessage("§aSuccessfully updated project name to §e" + optional.get().getName());
//	}, Bukkit.getScheduler().getMainThreadExecutor(plugin));

	private static final Component UPDATE_ERROR = Component.text("An error occurred while updating the project.").color(NamedTextColor.RED);

	@CommandDescription("Edits a projects name")
	@CommandMethod("project|projects edit <project> name <name>")
	public void onEditName(final @NonNull CommandSender commandSender,
						   @Argument(value = "project", description = "Project") final @NonNull Project project,
						   @Argument(value = "name", description = "Name") final @NonNull @Greedy String name) {
		project.setName(name);
		final CompletableFuture<Optional<Project>> promise = EpicJobs.get().getStorage().updateProject(project);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Updated project name to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getName()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

	@CommandDescription("Edits a projects leaders")
	@CommandMethod("project|projects edit <project> leader <leader>")
	public void onEditLeader(final @NonNull CommandSender commandSender,
							 @Argument(value = "project", description = "Project") final @NonNull Project project,
							 @Argument(value = "leader", description = "Leader") final @NonNull OfflinePlayer leader) {
		project.setLeader(leader.getUniqueId());
		final CompletableFuture<Optional<Project>> promise = EpicJobs.get().getStorage().updateProject(project);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Transferred project leadership to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(leader.getName()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

	@CommandDescription("Edits a projects location site")
	@CommandMethod("project|projects edit <project> location")
	public void onEditLocation(final @NonNull Player player,
							   @Argument(value = "project", description = "Project") final @NonNull Project project) {
		project.setLocation(player.getLocation());
		final CompletableFuture<Optional<Project>> promise = EpicJobs.get().getStorage().updateProject(project);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(UPDATE_ERROR);
				return;
			}
			player.sendMessage(Component.text("Updated the project site to your location").color(Colors.HONEY_YELLOW));
		});
	}

	@CommandDescription("Edits a projects status")
	@CommandMethod("project|projects edit <project> status <status>")
	public void onEditStatus(final @NonNull CommandSender commandSender,
							 @Argument(value = "project", description = "Project") final @NonNull Project project,
							 @Argument(value = "status", description = "Status") final @NonNull ProjectStatus status) {
		project.setProjectStatus(status);
		final CompletableFuture<Optional<Project>> promise = EpicJobs.get().getStorage().updateProject(project);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Set the project status to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getProjectStatus().name()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

}