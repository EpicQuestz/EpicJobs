package com.epicquestz.epicjobs.project;

import com.google.common.collect.ImmutableList;
import com.epicquestz.epicjobs.EpicJobs;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private final EpicJobs plugin;

    private final List<Project> projects;

    public ProjectManager(final EpicJobs plugin) {
        this.plugin = plugin;
        this.projects = new ArrayList<>();
    }

    public void firstLoad() {
        plugin.getStorage().loadAllProjects();
    }

    public List<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    public Project getProjectById(final int id) {
        for (final Project project : projects) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }

    public Project getProjectByName(final String name) {
        for (final Project project : projects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }

    public void addProject(final Project project) {
        projects.add(project);
    }

    public void removeProject(final Project project) {
        projects.remove(project);
    }

    public List<Project> getOpenProjects() {
        final List<Project> projectList = new ArrayList<>();
        for (final Project project : projects) {
            if (project.getProjectStatus().equals(ProjectStatus.ACTIVE)) {
                projectList.add(project);
            }
        }
        return ImmutableList.copyOf(projectList);
    }

}
