package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.user.EpicJobsPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import java.util.List;
import java.util.Optional;

import static com.epicquestz.epicjobs.constants.Messages.MISSING_PROFILE;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_MULITPLE_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PLAYER_HAS_NO_JOBS;

public class TeleportJobCommand {

	private final EpicJobs plugin;

	public TeleportJobCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("Teleport to a job site")
	@Permission(CommandPermissions.TELEPORT_JOB)
	@Command("job|jobs teleport|tp [job]")
	public void onCommand(final @NonNull Player player,
						  @Argument(value = "job", description = "Job", suggestions = "player-job") final @Nullable Job job) {
		if (job != null) {
			job.teleport(player);
			return;
		}

		final Optional<EpicJobsPlayer> optionalProfile = plugin.getEpicJobsPlayer(player);
		if (optionalProfile.isEmpty()) {
			MISSING_PROFILE.send(player);
			return;
		}

		final List<Job> jobs = optionalProfile.get().getJobs();
		if (jobs.isEmpty()) {
			PLAYER_HAS_NO_JOBS.send(player);
			return;
		}

		if (jobs.size() == 1) {
			jobs.get(0).teleport(player);
			return;
		}

		PLAYER_HAS_MULITPLE_JOBS.send(player);
	}

}
