package de.stealwonders.epicjobs.user;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private final List<Job> jobs;

    public User(@Nonnull final UUID uuid, @Nullable final String name) {
        this.uuid = uuid;
        this.name = name; // todo: figure out why this is nullable!?!?
        this.jobs = new ArrayList<>();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
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

    public List<Job> getActiveJobs() {
        final List<Job> jobList = new ArrayList<>();
        for (final Job job : jobs) {
            if (job.getJobStatus().equals(JobStatus.TAKEN)) {
                jobList.add(job);
            }
        }
        return ImmutableList.copyOf(jobList);
    }

    public List<Job> getCompletedJobs() {
        final List<Job> jobList = new ArrayList<>();
        for (final Job job : jobs) {
            if (job.getJobStatus().equals(JobStatus.COMPLETE)) {
                jobList.add(job);
            }
        }
        return ImmutableList.copyOf(jobList);
    }

}
