package de.stealwonders.epicjobs.storage;

import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobDifficulty;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Storage {

	private final List<Project> projects;
	private final List<Job> jobs;

	public Storage() {
		this.projects = new ArrayList<>();
		this.jobs = new ArrayList<>();
	}

	public boolean projectExists(final String tag) {
		return projects.stream().anyMatch(project -> project.getTag().equalsIgnoreCase(tag));
	}

	public CompletableFuture<Optional<Project>> createProject(final String tag,
															  final String name,
															  final Player leader) {
		return CompletableFuture.supplyAsync(() -> {
			// todo: save to database -> load from database -> create object
			final Project project = new Project(tag, name, leader);
			projects.add(project); // todo: make thread safe
			return Optional.of(project);
		});
	}

	public CompletableFuture<Optional<Job>> createJob(final Player creator,
													  final String description,
													  final Project project,
													  final JobCategory jobCategory,
													  final JobDifficulty jobDifficulty) {
		return CompletableFuture.supplyAsync(() -> {
			// todo: save to database -> load from database -> create object
			// generate a random integer id
			final int id = (int) (Math.random() * 1000000); // todo: database ;-)
			final Job job = new Job(id, creator, description, project, jobCategory, jobDifficulty);
			jobs.add(job); // todo: make thread safe
			return Optional.of(job);
		});
	}

	public CompletableFuture<Optional<Project>> updateProject(final Project project) {
		return CompletableFuture.supplyAsync(() -> {
			return Optional.of(project); // todo: db
		});
	}

	public CompletableFuture<Optional<Job>> updateJob(final Job job) {
		return CompletableFuture.supplyAsync(() -> {
			return Optional.of(job);  // todo: db
		});
	}

	public List<Project> getProjects() {
		return projects;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	// dummy method
	public static CompletableFuture<Optional<Project>> project() {
		return CompletableFuture.supplyAsync(Optional::empty);
	}

	// dummy method
	public static CompletableFuture<Optional<Job>> job() {
		return CompletableFuture.supplyAsync(Optional::empty);
	}

}