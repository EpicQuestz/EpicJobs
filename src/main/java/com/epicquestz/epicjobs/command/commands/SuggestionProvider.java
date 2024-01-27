package com.epicquestz.epicjobs.command.commands;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.user.EpicJobsPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.Optional;
import java.util.stream.Stream;

public class SuggestionProvider {

	private final EpicJobs plugin;

	public SuggestionProvider(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@Suggestions("player-job")
	public Stream<String> playerJobSuggestions(final @NonNull Player player) {
		final Optional<EpicJobsPlayer> optionalProfile = plugin.getEpicJobsPlayer(player);
		if (optionalProfile.isEmpty()) {
			return Stream.empty();
		}

		return optionalProfile.get().getJobs().stream()
				.map(job -> String.valueOf(job.getId()));
	}

	@Suggestions("open-job")
	public Stream<String> openJobSuggestions() {
		return plugin.getJobManager().getOpenJobs().stream()
				.map(job -> String.valueOf(job.getId()));
	}

}
