package de.stealwonders.epicjobs.command.commands.job;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record JobStatusCommands(EpicJobs plugin) {

	private static final Component UPDATE_ERROR = Component.text("An error occurred during execution! Please contact an administrator.").color(NamedTextColor.RED);

	@CommandDescription("Claim a job")
	@CommandMethod("job|jobs claim|c <job>")
	public void onClaim(final @NonNull Player player,
						@Argument(value = "job", description = "Job") final @NonNull Job job) {
		if (job.getJobStatus() != JobStatus.OPEN) {
			player.sendMessage(Component.text("This job is not open to be claimed!").color(NamedTextColor.RED));
			return;
		}

		job.setClaimant(player.getUniqueId());
		job.setJobStatus(JobStatus.TAKEN);

		final CompletableFuture<Optional<Job>> promise = plugin.getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(UPDATE_ERROR);
				return;
			}

			final Component announcement = Component.text(player.getName()).color(Colors.HONEY_YELLOW)
					.appendSpace()
					.append(Component.text("has claimed job:").color(Colors.HONEY_YELLOW)
					.appendSpace()
					.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN)));
			Bukkit.broadcast(announcement);
		});
	}

	// todo: if single job is claimed, then it can be abandoned (no arg needed)
	@CommandDescription("Abandon a job")
	@CommandMethod("job|jobs abandon|a <job>")
	public void onAbandon(final @NonNull Player player,
						@Argument(value = "job", description = "Job") final @NonNull Job job) {
		if (job.getClaimant() == null || !job.getClaimant().equals(player.getUniqueId())) {
			player.sendMessage(Component.text("This job is not yours!").color(NamedTextColor.RED));
			return;
		}

		if (job.getJobStatus() != JobStatus.TAKEN) {
			player.sendMessage(Component.text("This is not an active job!").color(NamedTextColor.RED));
			return;
		}

		job.setClaimant(null);
		job.setJobStatus(JobStatus.OPEN);

		final CompletableFuture<Optional<Job>> promise = plugin.getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(UPDATE_ERROR);
				return;
			}

			final Component announcement = Component.text(player.getName()).color(Colors.HONEY_YELLOW)
					.appendSpace()
					.append(Component.text("has abandoned job:").color(Colors.HONEY_YELLOW))
					.appendSpace()
					.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN))
					.appendNewline()
					.append(Component.text("It is available to be claimed again.").color(Colors.HONEY_YELLOW));
			Bukkit.broadcast(announcement);
		});
	}

	// todo: if single job is claimed, then it can be marked done (no arg needed)
	@CommandDescription("Mark a job as done")
	@CommandMethod("job|jobs done|d <job>")
	public void onDone(final @NonNull Player player,
					   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		if (job.getClaimant() == null || !job.getClaimant().equals(player.getUniqueId())) {
			player.sendMessage(Component.text("This job is not yours!").color(NamedTextColor.RED));
			return;
		}

		if (job.getJobStatus() != JobStatus.TAKEN) {
			player.sendMessage(Component.text("This is not an active job!").color(NamedTextColor.RED));
			return;
		}

		job.setJobStatus(JobStatus.DONE);

		final CompletableFuture<Optional<Job>> promise = plugin.getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				player.sendMessage(UPDATE_ERROR);
				return;
			}

			final Component announcement = Component.text(player.getName()).color(Colors.HONEY_YELLOW)
					.appendSpace()
					.append(Component.text("has marked job:").color(Colors.HONEY_YELLOW))
					.appendSpace()
					.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN))
					.appendSpace()
					.append(Component.text("as done.").color(Colors.HONEY_YELLOW));
			Bukkit.broadcast(announcement);
		});
	}

	@CommandDescription("Mark a job as completed")
	@CommandMethod("job|jobs complete <job>")
	public void onDone(final @NonNull CommandSender commandSender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		if (job.getJobStatus() != JobStatus.DONE) {
			commandSender.sendMessage(Component.text("This job is not done!").color(NamedTextColor.RED));
			return;
		}

		job.setJobStatus(JobStatus.COMPLETE);

		final CompletableFuture<Optional<Job>> promise = plugin.getStorage().updateJob(job);
		promise.thenAccept(optional -> {
			if (optional.isEmpty()) {
				commandSender.sendMessage(UPDATE_ERROR);
				return;
			}

			final Component announcement = Component.text("Job").color(Colors.HONEY_YELLOW)
					.appendSpace()
					.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN))
					.appendSpace()
					.append(Component.text("has been marked as complete.").color(Colors.HONEY_YELLOW));
			Bukkit.broadcast(announcement);
		});
	}

	@CommandDescription("Reopen a job")
	@CommandMethod("job|jobs reopen <job>")
	public void onReopen(final @NonNull CommandSender commandSender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		final Component component = Component.text("Job").color(Colors.HONEY_YELLOW)
				.appendSpace()
				.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN))
				.appendSpace()
				.append(Component.text("has been reopened.").color(Colors.HONEY_YELLOW));

		switch (job.getJobStatus()) {
			case COMPLETE, TAKEN -> {
				job.setJobStatus(JobStatus.OPEN);
				job.setClaimant(null);
				final CompletableFuture<Optional<Job>> promise = plugin.getStorage().updateJob(job);
				promise.thenAccept(optional -> {
					if (optional.isEmpty()) {
						commandSender.sendMessage(UPDATE_ERROR);
						return;
					}
					Bukkit.broadcast(component);
				});
			}
			case DONE -> {
				job.setJobStatus(JobStatus.TAKEN);
				final CompletableFuture<Optional<Job>> promise = plugin.getStorage().updateJob(job);
				promise.thenAccept(optional -> {
					if (optional.isEmpty()) {
						commandSender.sendMessage(UPDATE_ERROR);
						return;
					}
					commandSender.sendMessage(component);
				});
			}
			default -> commandSender.sendMessage(Component.text("This job has not been completed or marked done!").color(NamedTextColor.RED));
		}
	}

	// todo: assign
	// todo: unassign

}