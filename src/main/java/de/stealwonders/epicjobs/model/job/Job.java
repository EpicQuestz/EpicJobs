package de.stealwonders.epicjobs.model.job;

import de.stealwonders.epicjobs.model.TimeTrackable;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class Job implements TimeTrackable {

	private final int id;
	private final long creationTime;
	private long lastUpdateTime;
    private final UUID creator;
    private @Nullable UUID claimant;
    private String description;
    private Project project;
    private Location location;
    private JobStatus jobStatus;
    private JobCategory jobCategory;
    private JobDifficulty jobDifficulty;

	public Job(
			final int id,
			final Player creator,
			final String description,
			final Project project,
			final JobCategory jobCategory,
			final JobDifficulty jobDifficulty
	) {
		this(id, creator.getUniqueId(), null, description, project, creator.getLocation(), JobStatus.OPEN, jobCategory, jobDifficulty);
	}

	public Job(
			final int id,
			final UUID creator,
			@Nullable final UUID claimant,
			final String description,
			final Project project,
			final Location location,
			final JobStatus jobStatus,
			final JobCategory jobCategory,
			final JobDifficulty jobDifficulty
	) {
		this.id = id;
		this.creationTime = System.currentTimeMillis();
		this.lastUpdateTime = System.currentTimeMillis();
		this.creator = creator;
		this.claimant = claimant;
		this.description = description;
		this.project = project;
		this.location = location;
		this.jobStatus = jobStatus;
		this.jobCategory = jobCategory;
		this.jobDifficulty = jobDifficulty;
		project.addJob(this);
	}

	public int getId() {
		return id;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	private void updateTime() {
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public UUID getCreator() {
		return creator;
	}

	public @Nullable UUID getClaimant() {
		return claimant;
	}

	public void setClaimant(@Nullable UUID claimant) {
		this.claimant = claimant;
		updateTime();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		updateTime();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		updateTime();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
		updateTime();
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
		updateTime();
	}

	public JobCategory getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(JobCategory jobCategory) {
		this.jobCategory = jobCategory;
		updateTime();
	}

	public JobDifficulty getJobDifficulty() {
		return jobDifficulty;
	}

	public void setJobDifficulty(JobDifficulty jobDifficulty) {
		this.jobDifficulty = jobDifficulty;
		updateTime();
	}

	@Override
	public String toString() {
		return "Job(id=" + getId() +
				", creator=" + creator +
				", claimant=" + claimant +
				", description='" + description + '\'' +
				", project=" + project +
				", location=" + location +
				", jobStatus=" + jobStatus +
				", jobCategory=" + jobCategory +
				", jobDifficulty=" + jobDifficulty +
				')';
	}

}