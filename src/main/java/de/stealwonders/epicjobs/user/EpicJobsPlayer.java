package de.stealwonders.epicjobs.user;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobStatus;

import java.util.*;
import java.util.stream.Collectors;

public class EpicJobsPlayer {

    private UUID uuid;
    private List<Job> jobs;

    public EpicJobsPlayer(UUID uuid) {
        this.uuid = uuid;
        this.jobs = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
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
        List<Job> jobList = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getJobStatus() == JobStatus.TAKEN) {
                jobList.add(job);
            }
        }
        return ImmutableList.copyOf(jobList);
    }

    public List<Job> getCompletedJobs() {
        List<Job> jobList = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getJobStatus() == JobStatus.COMPLETE) {
                jobList.add(job);
            }
        }
        return ImmutableList.copyOf(jobList);
    }

}
