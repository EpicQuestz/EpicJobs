package de.stealwonders.epicjobs.model.project;

import de.stealwonders.epicjobs.model.TimeTrackable;
import de.stealwonders.epicjobs.model.job.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project implements TimeTrackable {

	private final String tag;
	private final long creationTime;
	private long lastUpdateTime;
	private String name;
	private UUID leader;
	private Location location;
	private ProjectStatus projectStatus;
	private final List<Job> jobs;

	public Project(final String tag, final String name, final Player leader) {
		this(tag, name, leader.getUniqueId(), leader.getLocation(), ProjectStatus.ACTIVE);
	}

	public Project(
			final String tag,
			final String name,
			final UUID leader,
			final Location location,
			final ProjectStatus projectStatus
	) {
		this.tag = tag;
		this.creationTime = System.currentTimeMillis();
		this.lastUpdateTime = System.currentTimeMillis();
		this.name = name;
		this.leader = leader;
		this.location = location;
		this.projectStatus = projectStatus;
		this.jobs = new ArrayList<>();
	}

	public String getTag() {
		return tag;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateTime();
	}

	public UUID getLeader() {
		return leader;
	}

	public void setLeader(UUID leader) {
		this.leader = leader;
		updateTime();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
		updateTime();
	}

	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(ProjectStatus projectStatus) {
		this.projectStatus = projectStatus;
		updateTime();
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void addJob(Job job) {
		jobs.add(job);
		updateTime();
	}

	public void removeJob(Job job) {
		jobs.remove(job);
		updateTime();
	}

	@Override
	public String toString() {
		return "Project(tag=" + getTag() +
				", name='" + name + '\'' +
				", leader=" + leader +
				", location=" + location +
				", projectStatus=" + projectStatus +
				')';
	}

}