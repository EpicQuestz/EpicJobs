package de.stealwonders.epicjobs.model.project;

import de.stealwonders.epicjobs.EpicJobs;
import me.lucko.helper.promise.Promise;
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
        return projects;
    }

    public void addProject(final Project project) {
        projects.add(project);
    }

    public void removeProject(final Project project) {
        projects.remove(project);
    }

    public void firstLoad() {
        Promise<List<Project>> promise = plugin.getStorage().loadAllProjects();
        promise.thenAcceptSync(this.projects::addAll);
    }

    public @Nullable Project getProjectById(final int id) {
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

//    public List<Project> getProjectByStatus(final ProjectStatus projectStatus) {
//        final List<Project> projectList = new ArrayList<>();
//        for (final Project project : projects) {
//            if (project.getProjectStatus() == projectStatus) {
//                projectList.add(project);
//            }
//        }
//        return projectList;
//    }

}
