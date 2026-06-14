package com.epicquestz.epicjobs.command.commands.project;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.Palette;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.epicquestz.epicjobs.constants.Messages.ALREADY_DEPUTY;
import static com.epicquestz.epicjobs.constants.Messages.CANT_MANAGE_DEPUTIES;
import static com.epicquestz.epicjobs.constants.Messages.DEPUTY_ADDED;
import static com.epicquestz.epicjobs.constants.Messages.DEPUTY_REMOVED;
import static com.epicquestz.epicjobs.constants.Messages.LEADER_CANT_BE_DEPUTY;
import static com.epicquestz.epicjobs.constants.Messages.NOT_A_DEPUTY;
import static com.epicquestz.epicjobs.constants.Messages.NO_DEPUTIES;

@Command("project|projects")
public class ProjectDeputyCommand {

	private final EpicJobs plugin;

	public ProjectDeputyCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("Add a deputy to a project")
	@Permission(CommandPermissions.MODIFY_PROJECT_DEPUTIES)
	@Command("deputies|deputy add <project> <player>")
	public void onDeputyAdd(final @NonNull CommandSender sender,
							@Argument(value = "project", description = "Project") final @NonNull Project project,
							@Argument(value = "player", description = "Player", suggestions = "all-offline-players") final @NonNull OfflinePlayer player
	) {
		if (!canManageDeputies(sender, project)) {
			CANT_MANAGE_DEPUTIES.send(sender);
			return;
		}
		final UUID target = player.getUniqueId();
		if (project.isLeader(target)) {
			LEADER_CANT_BE_DEPUTY.send(sender);
			return;
		}
		if (project.isDeputy(target)) {
			ALREADY_DEPUTY.send(sender, Utils.getPlayerHolderText(target));
			return;
		}
		project.addDeputy(target);
		DEPUTY_ADDED.send(sender, Utils.getPlayerHolderText(target), project.getName());
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateProject(project)).execute();
	}

	@CommandDescription("Remove a deputy from a project")
	@Permission(CommandPermissions.MODIFY_PROJECT_DEPUTIES)
	@Command("deputies|deputy remove <project> <player>")
	public void onDeputyRemove(final @NonNull CommandSender sender,
							   @Argument(value = "project", description = "Project") final @NonNull Project project,
							   @Argument(value = "player", description = "Player", suggestions = "all-offline-players") final @NonNull OfflinePlayer player
	) {
		if (!canManageDeputies(sender, project)) {
			CANT_MANAGE_DEPUTIES.send(sender);
			return;
		}
		final UUID target = player.getUniqueId();
		if (!project.isDeputy(target)) {
			NOT_A_DEPUTY.send(sender, Utils.getPlayerHolderText(target));
			return;
		}
		project.removeDeputy(target);
		DEPUTY_REMOVED.send(sender, Utils.getPlayerHolderText(target), project.getName());
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateProject(project)).execute();
	}

	@CommandDescription("List a project's deputies")
	@Permission(CommandPermissions.INFO_PROJECT)
	@Command("deputies|deputy list <project>")
	public void onDeputyList(final @NonNull CommandSender sender,
							 @Argument(value = "project", description = "Project") final @NonNull Project project
	) {
		final List<UUID> deputies = project.getDeputies();
		if (deputies.isEmpty()) {
			NO_DEPUTIES.send(sender);
			return;
		}
		final List<Component> names = deputies.stream()
			.map(uuid -> Component.text(Utils.getPlayerHolderText(uuid), Palette.Role.INFO.accent()))
			.collect(Collectors.toList());
		final JoinConfiguration joinConfiguration = JoinConfiguration.builder()
			.separator(Component.text(", ", Palette.MUTED))
			.lastSeparator(Component.text(" and ", Palette.MUTED))
			.build();
		final Component message = Component.text("Deputies of ", Palette.MUTED)
			.append(Component.text(project.getName(), Palette.Role.INFO.body()))
			.append(Component.text(": ", Palette.MUTED))
			.append(Component.join(joinConfiguration, names));
		sender.sendMessage(Component.empty());
		sender.sendMessage(message);
		sender.sendMessage(Component.empty());
	}

	private boolean canManageDeputies(final CommandSender sender, final Project project) {
		if (sender instanceof Player player && project.isLeader(player.getUniqueId())) {
			return true;
		}
		return sender.hasPermission(CommandPermissions.MODIFY_PROJECT_DEPUTIES_BYPASS);
	}

}
