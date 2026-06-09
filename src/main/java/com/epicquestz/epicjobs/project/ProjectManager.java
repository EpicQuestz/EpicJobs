package com.epicquestz.epicjobs.project;

import com.google.common.collect.ImmutableList;
import com.epicquestz.epicjobs.EpicJobs;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory view of all projects. This collection must only be mutated from the server main
 * thread; database writes happen off-thread but never touch this list directly.
 */
public class ProjectManager {

    private final EpicJobs plugin;

    private final List<Project> projects;

    public ProjectManager(EpicJobs plugin) {
        this.plugin = plugin;
        this.projects = new ArrayList<>();
    }

    public void firstLoad() {
        plugin.getStorage().loadAllProjects();
    }

    public List<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    public @Nullable Project getProjectById(int id) {
        for (Project project : projects) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }

    public @Nullable Project getProjectByName(String name) {
        for (Project project : projects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public void removeProject(Project project) {
        projects.remove(project);
    }

}
