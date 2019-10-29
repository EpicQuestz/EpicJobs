package de.stealwonders.epicjobs.job;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        Connection connection = null;

        try {
            connection = plugin.getHikari().getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                UUID creator = UUID.fromString(resultSet.getString("creator"));
                UUID claimant = resultSet.getObject("claimant") != null ? UUID.fromString(resultSet.getString("claimant")) : null;
                Timestamp creationTime = resultSet.getTimestamp("creationtime");
                String description = resultSet.getString("description");
                Project project = plugin.getProjectManager().getProjectById(resultSet.getInt("project"));
                Location location = Utils.deserializeLocation(resultSet.getString("location"));
                JobStatus jobStatus = JobStatus.valueOf(resultSet.getString("jobstatus"));
                JobCategory jobCategory = JobCategory.valueOf(resultSet.getString("jobcategory"));

                Job job = new Job(id, creator, claimant, creationTime.getTime(), description, project, location, jobStatus, jobCategory);
                jobs.add(job);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
