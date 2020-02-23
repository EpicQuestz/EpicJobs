package de.stealwonders.epicjobs.storage;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;

import java.util.UUID;
import java.util.concurrent.*;

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

    public CompletableFuture<EpicJobsPlayer> loadPlayer(UUID uniqueId) {
        return null;
    }

    public CompletableFuture<Project> createAndLoadProject(Project project) {
        return makeFuture(() -> this.implementation.createAndLoadProject(project));
    }

    public void loadAllProjects() {
        implementation.loadAllProjects();
    }

    public CompletableFuture<Void> saveProject(Project project) {
        return makeFuture(() -> this.implementation.saveProject(project));
    }

    public CompletableFuture<Void> deleteProject(Project project) {
        return makeFuture(() -> this.implementation.deleteProject(project));
    }

    public CompletableFuture<Job> createAndLoadJob(Job job) {
        return makeFuture(() -> this.implementation.createAndLoadJob(job));
    }

    public void loadAllJobs() {
        implementation.loadAllJobs();
    }

    public CompletableFuture<Void> saveJob(Job job) {
        return makeFuture(() -> this.implementation.saveJob(job));
    }

    public CompletableFuture<Void> deleteJob(Job job) {
        return makeFuture(() -> this.implementation.deleteJob(job));
    }

}
