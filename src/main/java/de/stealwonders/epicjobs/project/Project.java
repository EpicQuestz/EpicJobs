package de.stealwonders.epicjobs.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.job.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {

    private int id;
    private String name;
    private UUID leader;
    private long creationTime;
    private Location location;
    private ProjectStatus projectStatus;
    private List<Job> jobs;

    public Project(int id, String name, Player leader) {
        this(id, name, leader.getUniqueId(), System.currentTimeMillis(), leader.getLocation(), ProjectStatus.ACTIVE);
    }

    public Project(int id, String name, UUID leader, long creationTime, Location location, ProjectStatus projectStatus) {
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

    public void setName(String name) {
        this.name = name;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = leader.getUniqueId();
    }

    public long getCreationTime() {
        return creationTime;
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
        return ImmutableList.copyOf(jobs);
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public void removeJob(Job job) {
        jobs.remove(job);
    }

    public void teleport(Player player) {
        player.teleportAsync(this.getLocation());
    }

}
