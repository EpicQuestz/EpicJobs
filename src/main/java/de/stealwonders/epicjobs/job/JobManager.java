package de.stealwonders.epicjobs.job;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;

import java.util.ArrayList;
import java.util.List;

public class JobManager {

    private EpicJobs plugin;

    private List<Job> jobs;


    public JobManager(final EpicJobs plugin) {
        System.out.println("----- JobManager");
        this.plugin = plugin;
        this.jobs = new ArrayList<>();
    }

    public void firstLoad() {
        plugin.getStorageImplementation().loadAllJobs();
        for (final Job job : jobs) {
            System.out.println(job.getId());
            System.out.println(job.getCreator());
            System.out.println(job.getClaimant());
            System.out.println(job.getCreationTime());
            System.out.println(job.getDescription());
            System.out.println(job.getProject().getName());
            System.out.println(job.getLocation());
            System.out.println(job.getJobStatus());
            System.out.println(job.getJobCategory());
        }

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
            if (job.getJobStatus() == JobStatus.OPEN) {
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
