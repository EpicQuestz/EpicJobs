package de.stealwonders.epicjobs.storage;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Location;

import java.util.UUID;

public class Storage {

    private EpicJobs plugin;
    private StorageImplementation implementation;

    public Storage(EpicJobs plugin, StorageImplementation implementation) {
        this.plugin = plugin;
        this.implementation = implementation;
    }

    public void init() {
        this.implementation.init();
    }

    public void shutdown() {
        this.implementation.shutdown();
    }

    public EpicJobsPlayer loadPlayer(UUID uniqueId) {
        return null;
    }

    public Project createAndLoadProject(String name, UUID leader, Location location, ProjectStatus projectStatus) {
        return this.implementation.createAndLoadProject(name, leader, location, projectStatus);
    }

    public void loadAllProjects() {
        implementation.loadAllProjects();
    }

    public void saveProject(Project project) {
        this.implementation.saveProject(project);
    }

    public void deleteProject(Project project) {
        this.implementation.deleteProject(project);
    }

    public Job createAndLoadJob(UUID creator, UUID claimant, String description, Project project, Location location, JobStatus jobStatus, JobCategory jobCategory) {
        return this.implementation.createAndLoadJob(creator, claimant, description, project, location, jobStatus, jobCategory);
    }

    public void loadAllJobs() {
        implementation.loadAllJobs();
    }

    public void saveJob(Job job) {
        this.implementation.saveJob(job);
    }

    public void deleteJob(Job job) {
        this.implementation.deleteJob(job);
    }

}
