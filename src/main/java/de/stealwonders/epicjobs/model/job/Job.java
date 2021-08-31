package de.stealwonders.epicjobs.model.job;

import de.stealwonders.epicjobs.model.StorageEntity;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class Job extends StorageEntity {

    private final UUID creator;
    private @Nullable UUID claimant;
    private String description;
    private Project project;
    private Location location;
    private JobStatus jobStatus;
    private JobCategory jobCategory;
    private JobDifficulty jobDifficulty;

    public Job(
        final int id, // todo: figure out if we need to pass this everywhere
        @NonNull final Player creator,
        @NonNull final String description,
        @NonNull final Project project,
        @NonNull final JobCategory jobCategory,
        @NonNull final JobDifficulty jobDifficulty
    ) {
        this(id, System.currentTimeMillis(), System.currentTimeMillis(), creator.getUniqueId(), null, description, project, creator.getLocation(), JobStatus.OPEN, jobCategory, jobDifficulty);
    }

    public Job(
        final int id,
        final long creationTime,
        final long updateTime,
        @NonNull final UUID creator,
        @Nullable final UUID claimant,
        @NonNull final String description,
        @NonNull final Project project,
        @NonNull final Location location,
        @NonNull final JobStatus jobStatus,
        @NonNull final JobCategory jobCategory,
        @NonNull final JobDifficulty jobDifficulty
    ) {
        super(id, creationTime, updateTime);
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

    public UUID getCreator() {
        return creator;
    }

    public @Nullable UUID getClaimant() {
        return claimant;
    }

    public void setClaimant(@Nullable UUID claimant) {
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

    public JobDifficulty getJobDifficulty() {
        return jobDifficulty;
    }

    public void setJobDifficulty(JobDifficulty jobDifficulty) {
        this.jobDifficulty = jobDifficulty;
    }

    //    public void claim(@Nonnull User player) {
//        this.setClaimant(player.getUniqueId());
//        this.setJobStatus(JobStatus.TAKEN);
//        player.addJob(this);
//    }
//
//    public void abandon(@Nonnull User player) {
//        this.setClaimant(null);
//        this.setJobStatus(JobStatus.OPEN);
//        player.removeJob(this);
//    }

//    public void teleport(@Nonnull Player player) {
//        player.teleportAsync(this.getLocation());
//        Messages.PLAYER_JOB_TELEPORT.send(player, getId());
//    }

}
