package de.stealwonders.epicjobs.model.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.constants.Messages;
import de.stealwonders.epicjobs.model.StorageEntity;
import de.stealwonders.epicjobs.model.job.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Project extends StorageEntity {

    private String name;
    private final List<UUID> leaders;
    private Location location;
    private ProjectStatus projectStatus;
    private final List<Job> jobs;

    public Project(int id, @Nonnull String name, @Nonnull Player leader) {
        this(id, name, Collections.singletonList(leader.getUniqueId()), System.currentTimeMillis(), System.currentTimeMillis(), leader.getLocation(), ProjectStatus.ACTIVE);
    }

    public Project(int id, @Nonnull String name, @Nonnull List<UUID> leaders, long creationTime, long updateTime, @Nonnull Location location, @Nonnull ProjectStatus projectStatus) {
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

    public void addLeader(@Nonnull Player player) {
        leaders.add(player.getUniqueId());
    }

    public void removeLeader(@Nonnull Player player) {
        leaders.remove(player.getUniqueId());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(@Nonnull Location location) {
        this.location = location;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(@Nonnull ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public List<Job> getJobs() {
        return ImmutableList.copyOf(jobs);
    }

    public void addJob(@Nonnull Job job) {
        jobs.add(job);
    }

    public void removeJob(@Nonnull Job job) {
        jobs.remove(job);
    }

    public void teleport(@Nonnull Player player) {
        player.teleportAsync(this.getLocation());
        Messages.PLAYER_PROJECT_TELEPORT.send(player, getName());
    }

}
