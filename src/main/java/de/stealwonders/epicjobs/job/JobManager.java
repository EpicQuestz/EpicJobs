package de.stealwonders.epicjobs.job;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;

import java.util.ArrayList;
import java.util.List;

public class JobManager {

    private EpicJobs plugin;

    private List<Job> jobs;

    public JobManager(final EpicJobs plugin) {
        this.plugin = plugin;
        this.jobs = new ArrayList<>();
    }

    public void firstLoad() {
        plugin.getStorageImplementation().loadAllJobs();
    }

    public List<Job> getJobs() {
        return ImmutableList.copyOf(jobs);
    }

    public Job getJobById(final int id) {
        for (final Job job : jobs) {
            if (job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public void addJob(final Job job) {
        jobs.add(job);
    }

    public void removeJob(final Job job) {
        jobs.remove(job);
    }

    public List<Job> getOpenJobs() {
        final List<Job> jobList = new ArrayList<>();
        for (final Job job : jobs) {
            if (job.getJobStatus().equals(JobStatus.OPEN)) {
                jobList.add(job);
            }
        }
        return ImmutableList.copyOf(jobList);
    }

    public int getFreeId() {
        int maxNumber = 0;
        for (final Job job : jobs) {
            if (job.getId() > maxNumber) {
                maxNumber = job.getId();
            }
        }
        return maxNumber + 1;
    }

}
