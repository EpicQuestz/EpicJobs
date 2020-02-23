package de.stealwonders.epicjobs.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private EpicJobs plugin;

    private List<Project> projects;

    public ProjectManager(EpicJobs plugin) {
        System.out.println("----- ProjectManager");
        this.plugin = plugin;
        projects = new ArrayList<>();
//        fetchProjects();
        plugin.getStorageImplementation().loadAllProjects();

        //todo debuging
        for (Project project : projects) {
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

    public Project getProjectById(int id) {
        for (Project project : projects) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }

    public Project getProjectByName(String name) {
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

    public List<Project> getOpenProjects() {
        List<Project> projectList = new ArrayList<>();
        for (Project project : projects) {
            if (project.getProjectStatus() == ProjectStatus.ACTIVE) {
                projectList.add(project);
            }
        }
        return ImmutableList.copyOf(projectList);
    }

    public int getFreeId() {
        int maxNumber = 0;
        for (Project project : projects) {
            if (project.getId() > maxNumber) {
                maxNumber = project.getId();
            }
        }
        return maxNumber + 1;
    }
}
