package de.stealwonders.epicjobs.command.commands.project;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.specifier.Greedy;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.project.Project;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@CommandPermission(CommandPermissions.CREATE_PROJECT)
public record CreateProjectCommand(EpicJobs plugin) {

	private static final Component ALREADY_EXISTS = Component.text("A project with this tag already exists.").color(NamedTextColor.RED);
	private static final Component CREATE_ERROR = Component.text("An error occurred while creating the job.").color(NamedTextColor.RED);

	@CommandDescription("Creates a project")
	@ProxiedBy("createproject")
	@CommandMethod("project|projects create <tag> [name]")
	public void onCommand(final @NonNull Player player,
						  @Argument(value = "tag", description = "Tag") @NonNull String tag,
						  @Argument(value = "name", description = "Name") @Nullable @Greedy String name) {
		tag = tag.toLowerCase(); // We want the tag to always be lowercase (looks cleaner)
		if (plugin.getStorage().projectExists(tag)) {
			player.sendMessage(ALREADY_EXISTS);
			return;
		}

		if (name == null) {
			name = tag;
		}

		CompletableFuture<Optional<Project>> promise = plugin.getStorage().createProject(tag, name, player);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(CREATE_ERROR);
				return;
			}
			player.sendMessage(Component.text("Created project:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getName()).color(Colors.CARIBBEAN_GREEN)));
		});
	}
}