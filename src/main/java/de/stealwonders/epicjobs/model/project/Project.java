package de.stealwonders.epicjobs.model.project;

import de.stealwonders.epicjobs.model.StorageEntity;
import de.stealwonders.epicjobs.model.job.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Project extends StorageEntity {

    private String name;
    private final List<UUID> leaders;
    private Location location;
    private ProjectStatus projectStatus;
    private final List<Job> jobs;

    public Project(final int id, @NonNull final String name, @NonNull final Player leader) {
        this(id, System.currentTimeMillis(), System.currentTimeMillis(), name, Collections.singletonList(leader.getUniqueId()), leader.getLocation(), ProjectStatus.ACTIVE);
    }

    public Project(
        final int id,
        final long creationTime,
        final long updateTime,
        @NonNull final String name,
        @NonNull final List<UUID> leaders,
        @NonNull final Location location,
        @NonNull final ProjectStatus projectStatus
    ) {
        super(id, creationTime, updateTime);
        this.name = name;
        this.leaders = leaders;
        this.location = location;
        this.projectStatus = projectStatus;
        this.jobs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getLeaders() {
        return leaders;
    }

    public void addLeader(Player player) {
        leaders.add(player.getUniqueId());
    }

    public void removeLeader(Player player) {
        leaders.remove(player.getUniqueId());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public void removeJob(Job job) {
        jobs.remove(job);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name) && Objects.equals(leaders, project.leaders) && Objects.equals(location, project.location) && projectStatus == project.projectStatus && Objects.equals(jobs, project.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, leaders, location, projectStatus, jobs);
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", leaders=" + leaders.toString() +
                ", location=" + location +
                ", projectStatus=" + projectStatus +
                ", jobs=" + jobs.toString() +
                '}';
    }

    //    public void teleport(@Nonnull Player player) {
//        player.teleportAsync(this.getLocation());
//        Messages.PLAYER_PROJECT_TELEPORT.send(player, getName());
//    }

}
