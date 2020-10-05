package de.stealwonders.epicjobs.storage.implementation;

import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.entity.Player;

import java.util.List;

public interface StorageImplementation {

    EpicJobs getPlugin();

    void init();

    void shutdown();

    Project createAndLoadProject(String name, Player leader);

//    Project loadProject(int id);

    List<Project> loadAllProjects();

    void updateProject(Project project);

    void deleteProject(Project project);

    Job createAndLoadJob(Player creator, String description, Project project, JobCategory jobCategory);

//    Job loadJob(int id);

    List<Job> loadAllJobs();

    void updateJob(Job job);

    void deleteJob(Job job);

}
