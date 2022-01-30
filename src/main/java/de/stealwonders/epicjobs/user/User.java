package de.stealwonders.epicjobs.user;

import de.stealwonders.epicjobs.model.job.Job;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private final List<Job> jobs;

    public User(@Nonnull final Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public User(@Nonnull final UUID uuid, @Nonnull final String name) {
        this.uuid = uuid;
        this.name = name;
        this.jobs = new ArrayList<>();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
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

    //
//    public Optional<String> getName() {
//        return Optional.ofNullable(name);
//    }
//
//    public List<Job> getJobs() {
//        return ImmutableList.copyOf(jobs);
//    }
//
//    public void addJob(Job job) {
//        jobs.add(job);
//    }
//
//    public void removeJob(Job job) {
//        jobs.remove(job);
//    }
//
//    public List<Job> getActiveJobs() {
//        final List<Job> jobList = new ArrayList<>();
//        for (final Job job : jobs) {
//            if (job.getJobStatus().equals(JobStatus.TAKEN)) {
//                jobList.add(job);
//            }
//        }
//        return ImmutableList.copyOf(jobList);
//    }
//
//    public List<Job> getCompletedJobs() {
//        final List<Job> jobList = new ArrayList<>();
//        for (final Job job : jobs) {
//            if (job.getJobStatus().equals(JobStatus.COMPLETE)) {
//                jobList.add(job);
//            }
//        }
//        return ImmutableList.copyOf(jobList);
//    }

}
