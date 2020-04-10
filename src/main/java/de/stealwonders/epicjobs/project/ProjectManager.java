package de.stealwonders.epicjobs.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private EpicJobs plugin;

    private List<Project> projects;

    public ProjectManager(final EpicJobs plugin) {
        System.out.println("----- ProjectManager");
        this.plugin = plugin;
        this.projects = new ArrayList<>();
    }

    public void firstLoad() {
        plugin.getStorageImplementation().loadAllProjects();
        for (final Project project : projects) {
            System.out.println(project.getId());
            System.out.println(project.getName());
            System.out.println(project.getLeader());
            System.out.println(project.getCreationTime());
            System.out.println(project.getLocation());
            System.out.println(project.getProjectStatus());
        }

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
            if (project.getProjectStatus() == ProjectStatus.ACTIVE) {
                projectList.add(project);
            }
        }
        return ImmutableList.copyOf(projectList);
    }

    public int getFreeId() {
        int maxNumber = 0;
        for (final Project project : projects) {
            if (project.getId() > maxNumber) {
                maxNumber = project.getId();
            }
        }
        return maxNumber + 1;
    }

}
