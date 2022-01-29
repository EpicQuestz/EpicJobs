package de.stealwonders.epicjobs.model.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProjectManager {

    private final EpicJobs plugin;
    private final List<Project> projects;

    public ProjectManager(final EpicJobs plugin) {
        this.plugin = plugin;
        this.projects = new ArrayList<>();
    }

    public List<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    public void addProject(final Project project) {
        projects.add(project);
    }

    public void removeProject(final Project project) {
        projects.remove(project);
    }

//    public void firstLoad() {
//        projects.addAll(plugin.getStorage().loadAllProjects());
//    }

    public @Nullable Project getProjectById(final long id) {
        for (final Project project : projects) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }

    public @Nullable Project getProjectByName(final String name) {
        for (final Project project : projects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }

    public List<Project> getProjectByStatus(final ProjectStatus projectStatus) {
        final List<Project> projectList = new ArrayList<>();
        for (final Project project : projects) {
            if (project.getProjectStatus() == projectStatus) {
                projectList.add(project);
            }
        }
        return projectList;
    }

}
