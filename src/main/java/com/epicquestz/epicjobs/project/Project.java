package com.epicquestz.epicjobs.project;

import com.google.common.collect.ImmutableList;
import com.epicquestz.epicjobs.constants.Messages;
import com.epicquestz.epicjobs.job.Job;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Project {

    /** A tag identifies a project in commands; the name is only for display and may contain spaces. */
    public static final int MAX_TAG_LENGTH = 32;
    public static final Pattern TAG_PATTERN = Pattern.compile("[a-z0-9]+([_-][a-z0-9]+)*");

    private final int id;
    private final String tag;
    private String name;
    private UUID leader;
    private final long creationTime;
    private Location location;
    private ProjectStatus projectStatus;
    private final List<UUID> deputies;
    private final List<Job> jobs;

    public Project(int id, String tag, String name, UUID leader, long creationTime, Location location, ProjectStatus projectStatus, List<UUID> deputies) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.leader = leader;
        this.creationTime = creationTime;
        this.location = location;
        this.projectStatus = projectStatus;
        this.deputies = new ArrayList<>(deputies);
        jobs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public static boolean isValidTag(String tag) {
        return tag.length() <= MAX_TAG_LENGTH && TAG_PATTERN.matcher(tag).matches();
    }

    public String getTag() {
        return tag;
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

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public List<UUID> getDeputies() {
        return ImmutableList.copyOf(deputies);
    }

    public void addDeputy(UUID deputy) {
        if (!deputies.contains(deputy)) {
            deputies.add(deputy);
        }
    }

    public void removeDeputy(UUID deputy) {
        deputies.remove(deputy);
    }

    public boolean isLeader(UUID uuid) {
        return leader.equals(uuid);
    }

    public boolean isDeputy(UUID uuid) {
        return deputies.contains(uuid);
    }

    public boolean isLeaderOrDeputy(UUID uuid) {
        return isLeader(uuid) || isDeputy(uuid);
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
        Messages.PLAYER_PROJECT_TELEPORT.send(player, this.getName());
    }

}
