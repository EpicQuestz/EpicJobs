package de.stealwonders.epicjobs.job;

import co.aikar.idb.DbRow;
import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JobManager {

    private EpicJobs plugin;

    private List<Job> jobs;

    private static final String SELECT = "SELECT * FROM job;";

    public JobManager(EpicJobs plugin) {
        System.out.println("----- JobManager");
        this.plugin = plugin;
        jobs = new ArrayList<>();
        fetchJobs();

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

    private void fetchJobs() {

        CompletableFuture<List<DbRow>> row = plugin.getDatabase().getResultsAsync(SELECT);
        row.whenCompleteAsync((dbRows, throwable) -> dbRows.forEach(dbRow -> {

                int id = dbRow.getInt("id");
                UUID creator = UUID.fromString(dbRow.getString("creator"));
                UUID claimant = dbRow.get("claimant") != null ? UUID.fromString(dbRow.getString("claimant")) : null;
                Timestamp creationTime = Timestamp.valueOf(dbRow.getString("creationtime"));
                String description = dbRow.getString("description");
                Project project = plugin.getProjectManager().getProjectById(dbRow.getInt("project"));
                Location location = Utils.deserializeLocation(dbRow.getString("location"));
                JobStatus jobStatus = JobStatus.valueOf(dbRow.getString("jobstatus"));
                JobCategory jobCategory = JobCategory.valueOf(dbRow.getString("jobcategory"));

                Job job = new Job(id, creator, claimant, creationTime.getTime(), description, project, location, jobStatus, jobCategory);
                jobs.add(job);

        }));

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
