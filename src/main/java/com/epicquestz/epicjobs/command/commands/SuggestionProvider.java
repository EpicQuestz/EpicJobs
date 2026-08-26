package com.epicquestz.epicjobs.command.commands;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import com.epicquestz.epicjobs.user.User;
import com.epicquestz.epicjobs.utils.Utils;
import org.bukkit.command.CommandSender;
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
		final Optional<User> optionalProfile = plugin.getEpicJobsPlayer(player);
		if (optionalProfile.isEmpty()) {
			return Stream.empty();
		}

		return optionalProfile.get().getJobs().stream()
				.map(job -> String.valueOf(job.getId()));
	}

	@Suggestions("open-job")
	public Stream<String> openJobSuggestions() {
		return plugin.getJobManager().getJobs().stream()
				.filter(job -> job.getJobStatus().equals(JobStatus.OPEN))
				.map(job -> String.valueOf(job.getId()));
	}

	@Suggestions("active-project")
	public Stream<String> activeProjectSuggestions() {
		return plugin.getProjectManager().getProjects().stream()
				.filter(project -> project.getProjectStatus().equals(ProjectStatus.ACTIVE))
				.map(Project::getName);
	}

	@Suggestions("own-project")
	public Stream<String> ownProjectSuggestions(final @NonNull CommandSender sender) {
		return manageableProjects(sender, null);
	}

	@Suggestions("own-active-project")
	public Stream<String> ownActiveProjectSuggestions(final @NonNull CommandSender sender) {
		return manageableProjects(sender, ProjectStatus.ACTIVE);
	}

	@Suggestions("own-paused-project")
	public Stream<String> ownPausedProjectSuggestions(final @NonNull CommandSender sender) {
		return manageableProjects(sender, ProjectStatus.PAUSED);
	}

	private Stream<String> manageableProjects(final CommandSender sender, final ProjectStatus status) {
		return plugin.getProjectManager().getProjects().stream()
				.filter(project -> status == null || project.getProjectStatus().equals(status))
				.filter(project -> Utils.canManage(sender, project))
				.map(Project::getName);
	}

	@Suggestions("paused-project")
	public Stream<String> pausedProjectSuggestions() {
		return plugin.getProjectManager().getProjects().stream()
				.filter(project -> project.getProjectStatus().equals(ProjectStatus.PAUSED))
				.map(Project::getName);
	}

	@Suggestions("all-offline-players")
	public Stream<String> allOfflinePlayers() {
		return plugin.getPlayerCache().getNames().stream();
	}

}
