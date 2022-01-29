package de.stealwonders.epicjobs.storage.implementation;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobDifficulty;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.entity.Player;

import java.util.List;

public interface StorageImplementation {

    EpicJobs getPlugin();

    String getImplementationName();

    void init() throws Exception;

    void shutdown();

    Project createAndLoadProject(String name, Player leader) throws Exception;

    Project loadProject(int id) throws Exception;

    List<Project> loadAllProjects() throws Exception;

    void saveProject(Project project) throws Exception;

    void deleteProject(Project project) throws Exception;

    Job createAndLoadJob(Player creator, String description, Project project, JobCategory jobCategory, JobDifficulty jobDifficulty) throws Exception;

    Job loadJob(int id) throws Exception;

    List<Job> loadAllJobs() throws Exception;

    void saveJob(Job job) throws Exception;

    void deleteJob(Job job) throws Exception;

}
