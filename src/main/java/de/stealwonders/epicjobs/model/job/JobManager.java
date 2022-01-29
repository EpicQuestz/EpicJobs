package de.stealwonders.epicjobs.model.job;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JobManager {

    private final EpicJobs plugin;
    private final List<Job> jobs;

    public JobManager(final EpicJobs plugin) {
        this.plugin = plugin;
        this.jobs = new ArrayList<>();
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

//    public void firstLoad() {
//        jobs.addAll(plugin.getStorage().loadAllJobs());
//    }

    public @Nullable Job getJobById(final long id) {
        for (final Job job : jobs) {
            if (job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    public List<Job> getJobsByStatus(final JobStatus jobStatus) {
        final List<Job> jobList = new ArrayList<>();
        for (final Job job : jobs) {
            if (job.getJobStatus() == jobStatus) {
                jobList.add(job);
            }
        }
        return jobList;
    }

}
