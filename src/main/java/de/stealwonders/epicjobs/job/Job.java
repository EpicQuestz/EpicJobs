package de.stealwonders.epicjobs.job;

import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Job {

    private int id;
    private UUID creator;
    private UUID claimant;
    private long creationTime;
    private String description;
    private Project project;
    private Location location;
    private JobStatus jobStatus;
    private JobCategory jobCategory;

    public Job(final int id, final Player creator, final String description, final JobCategory jobCategory, final Project project) {
        this(id, creator.getUniqueId(), null, System.currentTimeMillis(), description, project, creator.getLocation(), JobStatus.OPEN, jobCategory);
    }

    public Job(final int id, final UUID creator, final UUID claimant, final long creationTime, final String description, final Project project, final Location location, final JobStatus jobStatus, final JobCategory jobCategory) {
        this.id = id;
        this.creator = creator;
        this.claimant = claimant;
        this.creationTime = creationTime;
        this.description = description;
        this.project = project;
        this.location = location;
        this.jobStatus = jobStatus;
        this.jobCategory = jobCategory;
        if (project != null) {
            project.addJob(this);
        }
    }

    public int getId() {
        return id;
    }

    public UUID getCreator() {
        return creator;
    }

    public UUID getClaimant() {
        return claimant;
    }

    public void setClaimant(final UUID claimant) {
        this.claimant = claimant;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(final JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public JobCategory getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(final JobCategory jobCategory) {
        this.jobCategory = jobCategory;
    }

    public void claim(final EpicJobsPlayer player) {
        this.setClaimant(player.getUuid());
        this.setJobStatus(JobStatus.TAKEN);
        player.addJob(this);
    }

    public void abandon(final EpicJobsPlayer player) {
        this.setClaimant(null);
        this.setJobStatus(JobStatus.OPEN);
        player.removeJob(this);
    }

    public void teleport(final Player player) {
        player.teleportAsync(this.getLocation());
    }

}
