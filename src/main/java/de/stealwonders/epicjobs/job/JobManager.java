package de.stealwonders.epicjobs.job;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;

import java.util.ArrayList;
import java.util.List;

public class JobManager {

    private EpicJobs plugin;

    private List<Job> jobs;


    public JobManager(EpicJobs plugin) {
        System.out.println("----- JobManager");
        this.plugin = plugin;
        jobs = new ArrayList<>();
//        fetchJobs();
        plugin.getStorageImplementation().loadAllJobs();

        //todo debuging
        for (Job job : jobs) {
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

    public Job getJobById(int id) {
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

    public List<Job> getOpenJobs() {
        List<Job> jobList = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getJobStatus() == JobStatus.OPEN) {
                jobList.add(job);
            }
        }
        return ImmutableList.copyOf(jobList);
    }

    public int getFreeId() {
        int maxNumber = 0;
        for (Job job : jobs) {
            if (job.getId() > maxNumber) {
                maxNumber = job.getId();
            }
        }
        return maxNumber + 1;
    }
}
