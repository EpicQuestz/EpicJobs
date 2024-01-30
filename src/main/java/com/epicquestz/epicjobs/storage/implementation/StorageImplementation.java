package com.epicquestz.epicjobs.storage.implementation;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import com.epicquestz.epicjobs.user.User;
import org.bukkit.Location;

import java.util.UUID;

public interface StorageImplementation {

    EpicJobs plugin();

    void init();

    void shutdown();

    User loadPlayer(UUID uniqueId);

    Project createAndLoadProject(String name, UUID leader, Location location, ProjectStatus projectStatus);

    Project loadProject(int id);

    void loadAllProjects();

    void updateProject(Project project);

    void deleteProject(Project project);

    Job createAndLoadJob(UUID creator, String description, Project project, Location location, JobStatus jobStatus, JobCategory jobCategory);

    Job loadJob(int id);

    void loadAllJobs();

    void updateJob(Job job);

    void deleteJob(Job job);

}
