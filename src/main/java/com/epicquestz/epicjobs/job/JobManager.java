package com.epicquestz.epicjobs.job;

import com.google.common.collect.ImmutableList;
import com.epicquestz.epicjobs.EpicJobs;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JobManager {

    private final EpicJobs plugin;

    private final List<Job> jobs;

    public JobManager(EpicJobs plugin) {
        this.plugin = plugin;
        this.jobs = new ArrayList<>();
    }

    public void firstLoad() {
        plugin.getStorage().loadAllJobs();
    }

    public List<Job> getJobs() {
        return ImmutableList.copyOf(jobs);
    }

    public @Nullable Job getJobById(int id) {
        for (Job job : jobs) {
            if (job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public void removeJob(Job job) {
        jobs.remove(job);
    }

}
