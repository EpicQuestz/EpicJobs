package de.stealwonders.epicjobs.storage;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobDifficulty;
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

    public String getName() {
        return implementation.getImplementationName();
    }

    public void init() {
        try {
            implementation.init();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Failed to init storage implementation");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            implementation.shutdown();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to shutdown storage implementation");
            e.printStackTrace();
        }
    }

    public Promise<Project> createAndLoadProject(String name, Player leader) {
        return Promise.supplyingAsync(() -> {
            try {
                return this.implementation.createAndLoadProject(name, leader);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

//    public Promise<Project> createAndLoadProject(String name, Player leader) {
//        return Promise.supplyingAsync(() -> {
//                return implementation.createAndLoadProject(name, leader);
//        }).supplyException();
//    }

    public Promise<List<Project>> loadAllProjects() {
        return Promise.supplyingAsync(() -> {
            try {
                return implementation.loadAllProjects();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public Promise<Void> saveProject(Project project) {
        return Promise.start().thenRunAsync(() -> {
            try {
                implementation.saveProject(project);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Promise<Void> deleteProject(Project project) {
        return Promise.start().thenRunAsync(() -> {
            try {
                implementation.deleteProject(project);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Promise<Job> createAndLoadJob(Player creator, String description, Project project, JobCategory jobCategory, JobDifficulty jobDifficulty) {
        return Promise.supplyingAsync(() -> {
            try {
                return implementation.createAndLoadJob(creator, description, project, jobCategory, jobDifficulty);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public Promise<List<Job>> loadAllJobs() {
        return Promise.supplyingAsync(() -> {
            try {
                return implementation.loadAllJobs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public Promise<Void> saveJob(Job job) {
        return Promise.start().thenRunAsync(() -> {
            try {
                implementation.saveJob(job);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Promise<Void> deleteJob(Job job) {
        return Promise.start().thenRunAsync(() -> {
            try {
                implementation.deleteJob(job);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
