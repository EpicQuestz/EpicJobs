package de.stealwonders.epicjobs.command.commands.job;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.job.Job;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TeleportJobCommand {

	@CommandDescription("Teleport to a job site")
	@CommandMethod("job|jobs teleport|tp <job>")
	public void onCommand(final @NonNull Player player,
						  @Argument(value = "job", description = "Job") final @NonNull Job job) {
		player.teleportAsync(job.getLocation());
		player.sendMessage(Component.text("Teleported to job:").color(Colors.HONEY_YELLOW)
				.append(Component.space())
				.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN)));
	}

}