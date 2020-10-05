package de.stealwonders.epicjobs.model.job;

import de.stealwonders.epicjobs.constants.Messages;
import de.stealwonders.epicjobs.model.StorageEntity;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class Job extends StorageEntity {

    private final UUID creator;
    private UUID claimant;
    private String description;
    private Project project;
    private Location location;
    private JobStatus jobStatus;
    private JobCategory jobCategory;

    public Job(int id, @Nonnull Player creator, @Nonnull String description, @Nonnull Project project, @Nonnull JobCategory jobCategory) {
        this(id, creator.getUniqueId(), null, System.currentTimeMillis(), System.currentTimeMillis(), description, project, creator.getLocation(), JobStatus.OPEN, jobCategory);
    }

    public Job(int id, @Nonnull UUID creator, @Nullable UUID claimant, long creationTime, long updateTime, @Nonnull String description, @Nonnull Project project, @Nonnull Location location, @Nonnull JobStatus jobStatus, @Nonnull JobCategory jobCategory) {
        super(id, creationTime, updateTime);
        this.creator = creator;
        this.claimant = claimant;
        this.description = description;
        this.project = project;
        this.location = location;
        this.jobStatus = jobStatus;
        this.jobCategory = jobCategory;
        project.addJob(this);
    }

    public UUID getCreator() {
        return creator;
    }

    public UUID getClaimant() {
        return claimant;
    }

    public void setClaimant(UUID claimant) {
        this.claimant = claimant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public JobCategory getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(JobCategory jobCategory) {
        this.jobCategory = jobCategory;
    }

    public void claim(@Nonnull User player) {
        this.setClaimant(player.getUniqueId());
        this.setJobStatus(JobStatus.TAKEN);
        player.addJob(this);
    }

    public void abandon(@Nonnull User player) {
        this.setClaimant(null);
        this.setJobStatus(JobStatus.OPEN);
        player.removeJob(this);
    }

    public void teleport(@Nonnull Player player) {
        player.teleportAsync(this.getLocation());
        Messages.PLAYER_JOB_TELEPORT.send(player, getId());
    }

}
