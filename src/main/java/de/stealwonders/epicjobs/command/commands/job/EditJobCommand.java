package de.stealwonders.epicjobs.command.commands.job;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobDifficulty;
import de.stealwonders.epicjobs.model.job.JobStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@CommandPermission(CommandPermissions.MODIFY_JOB)
public record EditJobCommand(EpicJobs plugin) {

	private static final Component UPDATE_ERROR = Component.text("An error occurred while updating the job.").color(NamedTextColor.RED);

	@CommandDescription("Edits a jobs claimant")
	@CommandMethod("job|jobs edit <job> claimant <claimant>")
	public void onEditClaimant(final @NonNull CommandSender commandSender,
							   @Argument(value = "job", description = "Job") final @NonNull Job job,
							   @Argument(value = "claimant", description = "Claimant") final @NonNull OfflinePlayer claimant) {
		job.setClaimant(claimant.getUniqueId());
		final CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Updated job claimant to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(claimant.getName()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

	@CommandDescription("Edits a jobs description")
	@CommandMethod("job|jobs edit <job> description <description>")
	public void onEditDescription(final @NonNull CommandSender commandSender,
								  @Argument(value = "job", description = "Job") final @NonNull Job job,
								  @Argument(value = "description", description = "Description") final @NonNull @Greedy String description) {
		job.setDescription(description);
		final CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Updated job description to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getDescription()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

	@CommandDescription("Edits a jobs location")
	@CommandMethod("job|jobs edit <job> location")
	public void onEditProject(final @NonNull Player player,
							  @Argument(value = "job", description = "Job") final @NonNull Job job) {
		job.setLocation(player.getLocation());
		final CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(UPDATE_ERROR);
				return;
			}
			player.sendMessage(Component.text("Updated the job site to your location").color(Colors.HONEY_YELLOW));
		});
	}

	@CommandDescription("Edits a jobs status")
	@CommandMethod("job|jobs edit <job> status <status>")
	public void onEditProject(final @NonNull CommandSender commandSender,
							  @Argument(value = "job", description = "Job") final @NonNull Job job,
							  @Argument(value = "status", description = "Status") final @NonNull JobStatus jobStatus) {
		job.setJobStatus(jobStatus);
		final CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Updated job status to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getJobStatus().name()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

	@CommandDescription("Edits a jobs category")
	@CommandMethod("job|jobs edit <job> category <category>")
	public void onEditCategory(final @NonNull CommandSender commandSender,
							  @Argument(value = "job", description = "Job") final @NonNull Job job,
							  @Argument(value = "category", description = "Category") final @NonNull JobCategory jobCategory) {
		job.setJobCategory(jobCategory);
		final CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Updated job category to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getJobCategory().name()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

	@CommandDescription("Edits a jobs difficulty")
	@CommandMethod("job|jobs edit <job> difficulty <difficulty>")
	public void onEditProject(final @NonNull CommandSender commandSender,
							  @Argument(value = "job", description = "Job") final @NonNull Job job,
							  @Argument(value = "difficulty", description = "Difficulty") final @NonNull JobDifficulty jobDifficulty) {
		job.setJobDifficulty(jobDifficulty);
		final CompletableFuture<Optional<Job>> promise = EpicJobs.get().getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}
			commandSender.sendMessage(Component.text("Updated job difficulty to:").color(Colors.HONEY_YELLOW)
					.append(Component.space())
					.append(Component.text(optional.get().getJobDifficulty().name()).color(Colors.CARIBBEAN_GREEN)));
		});
	}

}