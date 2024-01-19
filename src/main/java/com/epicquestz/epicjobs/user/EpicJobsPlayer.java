package com.epicquestz.epicjobs.user;

import com.google.common.collect.ImmutableList;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpicJobsPlayer {

    private final UUID uuid;
    private final List<Job> jobs;

    public EpicJobsPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.jobs = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
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
