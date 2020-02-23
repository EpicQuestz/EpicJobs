package de.stealwonders.epicjobs.storage.implementation;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Location;

import java.util.UUID;

public interface StorageImplementation {

    EpicJobs getPlugin();

    void init();

    void shutdown();

    EpicJobsPlayer loadPlayer(UUID uniqueId);

    Project createAndLoadProject(String name, UUID leader, Location location, ProjectStatus projectStatus);

    Project loadProject(int id);

    void loadAllProjects();

    void saveProject(Project project);

    void deleteProject(Project project);

    Job createAndLoadJob(UUID creator, UUID claimant, String description, Project project, Location location, JobStatus jobStatus, JobCategory jobCategory);

    Job loadJob(int id);

    void loadAllJobs();

    void saveJob(Job job);

    void deleteJob(Job job);
}
