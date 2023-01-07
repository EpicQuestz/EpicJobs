package de.stealwonders.epicjobs.command.commands.job;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.specifier.Greedy;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobDifficulty;
import de.stealwonders.epicjobs.model.project.Project;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@CommandPermission(CommandPermissions.CREATE_JOB)
public record CreateJobCommand(EpicJobs plugin) {

	private static final Component CREATE_ERROR = Component.text("An error occurred while creating the job.").color(NamedTextColor.RED);

	@CommandDescription("Creates a job")
	@ProxiedBy("createjob")
	@CommandMethod("job|jobs create <project> <category> <difficulty> <description>")
	public void onCommand(final @NonNull Player player,
						  @Argument(value = "project", description = "Project") final @NonNull Project project,
						  @Argument(value = "category", description = "Category") final @NonNull JobCategory jobCategory,
						  @Argument(value = "difficulty", description = "Difficulty") final @NonNull JobDifficulty jobDifficulty,
						  @Argument(value = "description", description = "Description") final @NonNull @Greedy String description) {
		CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().createJob(player, description, project, jobCategory, jobDifficulty);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(CREATE_ERROR);
				return;
			}
			player.sendMessage(Component.text("Created job:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text("#" + optional.get().getId()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

}