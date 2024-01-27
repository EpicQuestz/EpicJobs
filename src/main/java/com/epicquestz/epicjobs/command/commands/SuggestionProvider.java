package com.epicquestz.epicjobs.command.commands;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
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

	@Suggestions("active-project")
	public Stream<String> activeProjectSuggestions() {
		return plugin.getProjectManager().getProjects().stream()
				.filter(project -> project.getProjectStatus().equals(ProjectStatus.ACTIVE))
				.map(Project::getName);
	}

	@Suggestions("paused-project")
	public Stream<String> pausedProjectSuggestions() {
		return plugin.getProjectManager().getProjects().stream()
				.filter(project -> project.getProjectStatus().equals(ProjectStatus.PAUSED))
				.map(Project::getName);
	}

}
