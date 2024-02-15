package com.epicquestz.epicjobs.user;

import com.epicquestz.epicjobs.job.Job;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final List<Job> jobs;

    public User(final UUID uuid) {
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

}
