package de.stealwonders.epicjobs.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.constants.Messages;
import de.stealwonders.epicjobs.job.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {

    private final int id;
    private String name;
    private UUID leader;
    private final long creationTime;
    private Location location;
    private ProjectStatus projectStatus;
    private final List<Job> jobs;

    public Project(final int id, final String name, final Player leader) {
        this(id, name, leader.getUniqueId(), System.currentTimeMillis(), leader.getLocation(), ProjectStatus.ACTIVE);
    }

    public Project(final int id, final String name, final UUID leader, final long creationTime, final Location location, final ProjectStatus projectStatus) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.creationTime = creationTime;
        this.location = location;
        this.projectStatus = projectStatus;
        jobs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(final Player leader) {
        this.leader = leader.getUniqueId();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(final ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public List<Job> getJobs() {
        return ImmutableList.copyOf(jobs);
    }

    public void addJob(final Job job) {
        jobs.add(job);
    }

    public void removeJob(final Job job) {
        jobs.remove(job);
    }

    public void teleport(final Player player) {
        player.teleportAsync(this.getLocation());
        Messages.PLAYER_PROJECT_TELEPORT.send(player, getName());
    }

}
