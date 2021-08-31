package de.stealwonders.epicjobs.storage;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import me.lucko.helper.promise.Promise;
import org.bukkit.entity.Player;

import java.util.List;

public class Storage {

    private final EpicJobs plugin;
    private final StorageImplementation implementation;

    public Storage(EpicJobs plugin, StorageImplementation implementation) {
        this.plugin = plugin;
        this.implementation = implementation;
    }

    public StorageImplementation getImplementation() {
        return implementation;
    }

    public void init() {
        try {
            this.implementation.init();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to init storage implementation");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.implementation.shutdown();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to shutdown storage implementation");
            e.printStackTrace();
        }
    }

    public Promise<Project> createAndLoadProject(String name, Player leader) {
        return Promise.supplyingAsync(() -> this.implementation.createAndLoadProject(name, leader));
    }

    public Promise<List<Project>> loadAllProjects() {
        return Promise.supplyingAsync(this.implementation::loadAllProjects);
    }

    public Promise<Void> updateProject(Project project) {
        return Promise.start().thenRunAsync(() -> this.implementation.updateProject(project));
    }

    public Promise<Void> deleteProject(Project project) {
        return Promise.start().thenRunAsync(() -> this.implementation.deleteProject(project));
    }

    public Promise<Job> createAndLoadJob(Player creator, String description, Project project, JobCategory jobCategory) {
        return Promise.supplyingAsync(() -> this.implementation.createAndLoadJob(creator, description, project, jobCategory));
    }

    public Promise<List<Job>> loadAllJobs() {
        return Promise.supplyingAsync(this.implementation::loadAllJobs);
    }

    public Promise<Void> updateJob(Job job) {
        return Promise.start().thenRunAsync(() -> this.implementation.updateJob(job));
    }

    public Promise<Void> deleteJob(Job job) {
        return Promise.start().thenRunAsync(() -> this.implementation.deleteJob(job));
    }

}
